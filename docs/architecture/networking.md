# Networking

HTTP lives in `core/network`. Features never build their own client.

## Client

`NetworkModule` provides a singleton `Json` (`ignoreUnknownKeys`, `explicitNulls = false`), an
`OkHttpClient` (30 s connect/read/write timeouts) and a `Retrofit` bound to `BuildConfig.API_URL`
with the kotlinx-serialization converter. Debug builds log request and response bodies; the
`Authorization` header is redacted. Auth interceptors are added to this client by the auth layer.

## Base URL

`BuildConfig.API_URL` is resolved at build time, first match wins:

1. `api.url` in `local.properties`
2. environment variable `API_URL`
3. `https://dummyjson.com/`

Keep the trailing slash; Retrofit requires it.

## `@Anonymous`

Annotate a Retrofit method with `@Anonymous` to send it without the bearer token (login, refresh).
Interceptors check `request.isAnonymous()`.

## Errors

Wrap every call in `safeApiCall(json) { api.something() }`. It returns `ApiResult.Success(data)` or
`ApiResult.Failure(ApiError(message, status, isNetworkError))`:

| Cause | `message` | `status` | `isNetworkError` |
| --- | --- | --- | --- |
| HTTP error | `message` field of a JSON body, else null | HTTP code | false |
| `IOException` | null | null | true |
| Malformed response | null | null | false |

Cancellation is rethrown, never swallowed.

## Adding an API to a feature

1. Declare a Retrofit interface with `suspend` functions (relative paths).
2. Provide it from a feature Hilt module: `retrofit.create(MyApi::class.java)`.
3. Call it through `safeApiCall` from a repository and map `ApiError` to UI state.
