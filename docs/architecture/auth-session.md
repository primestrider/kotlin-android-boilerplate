# Auth session

## Storage

`SessionRepository` (`core/auth`) owns the signed-in `Session` (user and tokens). A user is signed in
if and only if a session exists.

- **What is stored:** one preferences key, `auth.session`, in its own DataStore file `session`
  (qualifier `@SessionStore`), separate from the `settings` file.
- **Encryption:** the session is serialized to JSON, encrypted with AES-256/GCM using a non-exportable
  key (`kotlinstarter_session`) generated in the AndroidKeyStore on first use, and stored as Base64
  (12-byte IV followed by ciphertext). `SessionCipher` is the seam; tests use a fake because JVM
  tests have no Keystore.
- **Backups:** `datastore/session.preferences_pb` is excluded from cloud backup and device transfer
  (`backup_rules.xml`, `data_extraction_rules.xml`). The Keystore key never leaves the device, so a
  restored ciphertext could not be decrypted anyway.
- **Failure handling:** startup never crashes on stored data.
  - A stored value that cannot be decoded, decrypted or parsed is treated as signed out and removed.
  - A storage read that fails with an `IOException` is treated as signed out for that run, but the
    stored value is kept, so the next start can still restore it.
  - Only permanent key loss (`KeyPermanentlyInvalidatedException`, `UnrecoverableKeyException`)
    deletes the Keystore key. Encrypt then generates a new key once; decrypt never regenerates
    (the old data is unrecoverable, so it falls under the first rule). Other keystore errors may be
    transient and keep the key.
- **Startup:** the session is read before the first frame. `AppViewModel.ready` waits for it and the
  theme, and the splash stays up until then, so a signed-in cold start never flashes sign-in.
- **Not persisted:** `signOutReason` lives in memory only, so the "session expired" message is not
  repeated on cold start.

## Requests

`NetworkModule` provides two clients with the same timeouts (30 s connect/read/write, 60 s per
call) and the same header logging. The logger is a network interceptor, so it also logs
authenticator retries; `Authorization` is redacted.

- **App client** (`OkHttpClient`, `Retrofit`): `AuthInterceptor` adds
  `Authorization: Bearer <accessToken>` while signed in, and `TokenAuthenticator` handles 401s.
  A Retrofit method annotated `@Anonymous` (`core/network`) is sent without the bearer. Use it
  for sign-in and any public endpoint.
- **Refresh client** (`@RefreshClient OkHttpClient`, `@RefreshRetrofit Retrofit`): its own
  `Dispatcher`, with no bearer and no authenticator. The adapter's refresh call must use it. While
  a refresh runs, the authenticator blocks the app client's dispatcher threads, which hold its
  per-host slots. A refresh sent through the app client would then wait for a slot forever.

## Refresh and expiry

`AuthAdapter` (`core/auth`) is the only backend-specific piece: `signIn(credentials)` and
`refresh(refreshToken)`. A feature module binds the implementation (the example binds
`DummyJsonAuthAdapter` in `feature/example/auth/ExampleAuthModule.kt`). `refresh` must be built on
`@RefreshRetrofit`, and must throw an `IOException` when the server cannot be reached.

```
request --AuthInterceptor (Bearer a1)--> server
        <-- 401 --
TokenAuthenticator
  gives up (401 reaches the caller) if: @Anonymous, no bearer, or this was already the retry
  else SessionRefresher.refresh(failed token "a1")
        signed out            -> SignedOut: give up, caller gets the 401
        stored token != "a1"  -> someone already refreshed: Refreshed(stored), retry with it
        refresh in flight     -> await that same attempt and share its outcome
        else adapter.refresh(refreshToken) over the refresh client
             tokens           -> updateTokens -> Refreshed("a2"): retry once with "Bearer a2"
             IOException      -> NetworkFailure: session kept, call fails with an IOException
                                 (safeApiCall: isNetworkError = true)
             anything else    -> signOut(EXPIRED) -> SignedOut: give up, caller gets the 401
retry 401 again -> authenticator gives up (priorResponse != null), caller gets the 401
```

- **Single flight:** the refresher keeps one in-flight `Deferred`. All concurrent 401s await that
  attempt and get the same outcome, so a flaky network costs one refresh attempt, not one per call.
  A call whose failed token is already outdated retries with the stored token without waiting.
- **Network failure is not expiry:** if the refresh cannot reach the server, the user stays signed
  in and the call reports a network error. A rejected refresh (an HTTP error) or any other failure
  signs out.
- **Expiry:** signing out with `SignOutReason.EXPIRED` triggers `SessionExpiryAnnouncer`, which is
  started in `KotlinStarterApp.onCreate`. It shows one error message (`auth_session_expired`) and
  clears the reason. The reason is memory-only, so the message is not repeated on cold start.
- **Cancellation:** the attempt runs in the application scope. Cancelling the call that started it
  does not cancel a refresh that is already in flight.
- **Never crashes a network thread:** the refresher catches every failure, including a storage error
  while signing out. The session then stays as it was and the caller gets the 401. Only
  `IOException`s escape the authenticator.
- **Dependency cycle:** the client needs the authenticator, which needs the refresher, which needs
  the adapter. `TokenAuthenticator` takes a `Provider<SessionRefresher>` to break the cycle.
