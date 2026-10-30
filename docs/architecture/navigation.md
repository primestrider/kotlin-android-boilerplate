# Navigation

The app uses Navigation Compose with type-safe routes (kotlinx.serialization) inside a bottom-navigation shell.

## Shell and tabs

`app/navigation/MainShell.kt` is the app's root composable (set by `MainActivity`). It is a `Scaffold` whose bottom bar is `AppNavigationBar` (`core/designsystem/component/`) and whose content is one `NavHost` with a nested graph per tab:

| Tab (`TopLevelTab`) | Graph | Start route | Contents |
| --- | --- | --- | --- |
| Home | `HomeGraph` | `HomeRoute` | example index, Form, styling pages |
| Components | `ComponentsGraph` | `ComponentsHomeRoute` | component example pages |
| Profile | `ProfileGraph` | `SettingsRoute` | Settings (Account joins with the auth plan) |

Behavior:

- Switching tabs uses `navigate(graph) { popUpTo(start) { saveState = true }; launchSingleTop = true; restoreState = true }`, so each tab keeps its own back stack.
- Re-selecting the current tab pops it to its start route.
- Back at the root of a non-Home tab switches to Home and restores Home's saved stack (a `BackHandler` composed after the `NavHost`, so Home's stale saved state is never resurrected later); back at Home's root leaves the app.
- The `NavHost` consumes the shell's bottom inset, so screens use `AppScaffold` normally without a double navigation-bar gap.

## Route objects

Each destination is a `@Serializable` object or data class:

- `HomeRoute` and the graph objects live in `app/navigation/AppNavHost.kt`.
- Example-feature routes live in `feature/example/ExampleRoutes.kt`, so deleting `feature/example` removes them.

## Registering a destination

1. Add a `@Serializable` route object.
2. Add `composable<MyRoute> { ... }` to the graph of the tab it belongs to (`homeGraph`, `componentsGraph`, `profileGraph` in `AppNavHost.kt`). Navigate only to destinations of the current tab (or switch tabs first): navigating into another tab's graph pushes the page onto the current stack and the selected tab flips to the declaring tab.
3. To list it on an index, add an `ExampleEntry(title, subtitle, route)` to `exampleSections` (Home) or `componentEntries` (Components) in `feature/example/ExampleCatalog.kt`.
4. A new top-level tab means a new `TopLevelTab` entry, a graph object and a `NavGraphBuilder` extension.

## Screen / Route split

- `XxxScreen(state, callbacks)` is stateless and tested with Robolectric.
- The `composable<Route>` block is the thin "route" layer: it obtains the `hiltViewModel()`, collects state with `collectAsStateWithLifecycle()`, and wires callbacks (including `navController.popBackStack()` for back).

## Adding a protected graph later

Wrap protected destinations in a nested graph, e.g. `navigation<ProtectedGraph>(startDestination = ...)`, and redirect to sign-in (`navigate { popUpTo(...) }`) from an auth state exposed by a ViewModel. Public routes (sign-in, onboarding) stay outside the tabs.
