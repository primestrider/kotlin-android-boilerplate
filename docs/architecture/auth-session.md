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

`NetworkModule` adds `AuthInterceptor` and `TokenAuthenticator` to the shared `OkHttpClient`.

- Every request gets `Authorization: Bearer <accessToken>` while signed in.
- A Retrofit method annotated `@Anonymous` (`core/network`) is sent without it. Use it for sign-in,
  refresh and any public endpoint.

## Refresh and expiry

`AuthAdapter` (`core/auth`) is the only backend-specific piece: `signIn(credentials)` and
`refresh(refreshToken)`. A feature module binds the implementation (the example binds
`DummyJsonAuthAdapter` in `feature/example/auth/ExampleAuthModule.kt`).

```
request --AuthInterceptor (Bearer a1)--> server
        <-- 401 --
TokenAuthenticator
  gives up (401 reaches the caller) if: @Anonymous, no bearer, or this was already the retry
  else SessionRefresher.refresh(failed token "a1")       (one mutex for all callers)
        stored token != "a1"  -> someone already refreshed: retry with the stored token
        signed out            -> give up
        else adapter.refresh(refreshToken) -> updateTokens -> retry once with "Bearer a2"
        refresh throws        -> signOut(EXPIRED) -> give up
retry 401 again -> authenticator gives up (priorResponse != null), caller gets the 401
```

- **Single flight:** concurrent 401s queue on the refresher's mutex. The first one refreshes; the
  others see that the stored token no longer matches the one that failed and retry with it, so the
  adapter is called once.
- **Expiry:** a failed refresh signs out with `SignOutReason.EXPIRED`. `SessionExpiryAnnouncer`,
  started in `KotlinStarterApp.onCreate`, shows one error message (`auth_session_expired`) and
  clears the reason. The reason is memory-only, so the message is not repeated on cold start.
- **Never crashes a network thread:** the refresher catches every failure, including a storage
  error while signing out (the session then stays as it was and the caller gets the 401).
- **Dependency cycle:** the client needs the authenticator, which needs the refresher, which needs
  the adapter, which needs the client. `TokenAuthenticator` takes a `Provider<SessionRefresher>`
  to break it.
