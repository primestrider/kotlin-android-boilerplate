# Architecture overview

Single-module app (`:app`) with the root package `com.primestrider.kotlinstarter`.

## Layers

- `app/`: application shell. `MainActivity`, app-level ViewModel, and the navigation host that wires every route.
- `core/*`: reusable building blocks (storage, design system, i18n, later networking and shared components). Knows nothing about features.
- `feature/*`: user-facing features. `feature/example` holds the demo content and can be deleted without breaking `core`.

## Dependency direction

```
app  ->  feature  ->  core
app  ->  core
```

`core` must never import `feature`. `feature` must not import `app`.

## Screen / Route / ViewModel split

- `XxxScreen(state, callbacks)`: stateless composable. Easy to test with Robolectric and to preview.
- `XxxRoute`: thin composable that obtains the ViewModel via `hiltViewModel()`, collects its `StateFlow`, and passes state and callbacks to the screen.
- `XxxViewModel`: holds UI state as `StateFlow`, talks to repositories (for example DataStore-backed preferences), and is injected by Hilt.

## Error handling

Three levels, from narrow to wide:

1. **Per screen.** A failed load is state, not an exception: ViewModels catch and map failures to `ApiError` (`safeApiCall`, `toApiError`, see `data-loading.md`) and screens render `LoadingState` / `EmptyState` / `ErrorState` (with Retry) from `core/designsystem/component/StateViews.kt`. A 404 for a single item is "not found", with no Retry.
2. **App-wide messages.** Short confirmations and non-blocking failures go through the injected `Messenger` (snackbar shown by `MessengerHost` in the shell), so ViewModels never need a `Context`.
3. **Crashes.** `core/crash/CrashRecorder` is installed first thing in `KotlinStarterApp.onCreate`. On an uncaught exception it stores the class name, a message truncated to 500 characters and the time (no stack trace) with a synchronous `commit()`, then always hands the crash to the previous handler, so the system and any crash reporter still see it and the process still dies. On the next launch `AppViewModel.lastCrash` is non-null and `MainActivity` shows `CrashFallbackScreen` instead of the shell; Continue clears it.

The recorder deliberately does not try to keep the process alive: after an uncaught exception the app's state is unknown, and continuing would hide the bug and risk corrupting data. A crash reporter (Firebase Crashlytics, Sentry) installs its own handler; install it before `CrashRecorder.install()` so the chain is recorder → reporter → system.

Debug builds have Settings → Developer → "Crash the app" to try it.
