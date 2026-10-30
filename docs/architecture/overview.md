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
