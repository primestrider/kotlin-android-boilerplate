# Networking

HTTP lives in `core/network`. Features never build their own client.

## Client

`NetworkModule` provides a singleton `Json` (`ignoreUnknownKeys`, `explicitNulls = false`) and two
clients, both bound to `BuildConfig.API_URL` (`@ApiBaseUrl HttpUrl`) with the kotlinx-serialization
converter:

- **App client** (`OkHttpClient`, `Retrofit`): what features use. `AuthInterceptor` adds the bearer
  token and `TokenAuthenticator` refreshes on 401 (see `auth-session.md`). Both act only on requests
  whose host is the `API_URL` host, so a call to any other host (an image CDN, a third-party API)
  never carries the token.
- **Refresh client** (`@RefreshClient OkHttpClient`, `@RefreshRetrofit Retrofit`): its own
  dispatcher, no bearer, no authenticator. Only the auth adapter's refresh call uses it.

Both have 30 s connect, read and write timeouts and a 60 s timeout for the whole call. Debug builds
log request and response headers only, never bodies (login and refresh carry passwords and
tokens). The logger is a network interceptor, so it sees the final headers, including
authenticator retries. `Authorization`, `Cookie` and `Set-Cookie` are redacted.

## Base URL

`BuildConfig.API_URL` is resolved at build time, first match wins:

1. `api.url` in `local.properties`
2. environment variable `API_URL`
3. `https://dummyjson.com/`

Keep the trailing slash; Retrofit requires it. A blank value counts as unset.

A plain `http://` URL (for example `http://10.0.2.2:3000/` for a local server from the emulator) is blocked by Android unless you add a network security config or cleartext allowance for it.

## `@Anonymous`

Annotate a Retrofit method with `@Anonymous` to send it without the bearer token (login, refresh).
Interceptors check `request.isAnonymous()`.

## Errors

Wrap every call in `safeApiCall(json) { api.something() }`. It returns `ApiResult.Success(data)` or
`ApiResult.Failure(ApiError(message, status, isNetworkError, data))`:

| Cause | `message` | `status` | `isNetworkError` | `data` |
| --- | --- | --- | --- | --- |
| HTTP error | `message` field of a JSON body, else null | HTTP code | false | the body as a `JsonElement` when it is valid JSON, else null |
| `IOException` | null | null | true | null |
| Malformed response | null | null | false | null |

Use `data` for structured error bodies, for example per-field validation errors of a 422.

Cancellation is rethrown, never swallowed.

## Adding an API to a feature

1. Declare a Retrofit interface with `suspend` functions (relative paths).
2. Provide it from a feature Hilt module: `retrofit.create(MyApi::class.java)`.
3. Call it through `safeApiCall` from a repository and map `ApiError` to UI state.
