# Navigation

The app uses Navigation Compose with type-safe routes (kotlinx.serialization).

## Route objects

Each destination is a `@Serializable` object or data class:

- `HomeRoute` lives in `app/navigation/AppNavHost.kt` (start destination).
- Example-feature routes (`SettingsRoute`, and later the styling pages) live in
  `feature/example/ExampleRoutes.kt`, so deleting `feature/example` removes them.

## Registering a destination

1. Add a `@Serializable` route object.
2. Add `composable<MyRoute> { ... }` inside the `NavHost` in `AppNavHost`.
3. To list it on the Home index, add an `ExampleEntry(title, subtitle, route)` to a section in
   `feature/example/ExampleCatalog.kt` (`exampleSections`). Append new sections in this order:
   Features, Components, SDK, Styling. Home calls `navController.navigate(entry.route)`.

## Screen / Route split

- `XxxScreen(state, callbacks)` is stateless and tested with Robolectric.
- The `composable<Route>` block in `AppNavHost` is the thin "route" layer: it obtains the
  `hiltViewModel()`, collects state with `collectAsStateWithLifecycle()`, and wires callbacks
  (including `navController.popBackStack()` for back).

## Adding a protected graph later

Wrap protected destinations in a nested graph, e.g. `navigation<ProtectedGraph>(startDestination = ...)`,
and choose the `NavHost` start destination (or redirect with `navigate { popUpTo(...) }`) from an
auth state exposed by a ViewModel. Public routes (login, onboarding) stay at the top level.
