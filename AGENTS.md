# AGENTS.md

Kotlin Android starter kit (Compose + Hilt), a port of a React Native boilerplate.

## Toolchain

AGP 9.4.0 (built-in Kotlin), Gradle 9.6.0, Kotlin 2.4.21, compileSdk/targetSdk 37, minSdk 24, JVM target 17.
All dependencies and plugins are declared in `gradle/libs.versions.toml`.

## Project map

Single module `:app`, root package `com.primestrider.kotlinstarter`.

```
KotlinStarterApp.kt          # @HiltAndroidApp
app/                         # MainActivity, AppViewModel, navigation (MainShell tabs + per-tab graphs in AppNavHost.kt)
core/storage/                # DataStore module and preference keys
core/network/                # Retrofit/OkHttp clients (app + refresh), ApiError, safeApiCall, toApiError, @Anonymous (docs/architecture/networking.md, data-loading.md)
core/auth/                   # encrypted SessionRepository, AuthAdapter seam, bearer + single-flight 401 refresh, expiry message (docs/architecture/auth-session.md)
core/designsystem/theme/     # palette, semantic colors, spacing, radii, typography, AppTheme
core/designsystem/component/ # reusable components (see docs/architecture/components.md)
core/i18n/                   # AppLanguage, LanguageController
core/util/                   # formatCurrency, formatPercent, currentLocale
feature/example/             # example catalog, Home, Profile hub, sign-in, protected Account, Settings, component + styling pages, form example (deletable)
feature/example/products/    # Products list + detail: Retrofit API, Paging 3 source, debounced search, tabs, add-to-cart sheet (docs/architecture/data-loading.md)
feature/example/auth/        # DummyJSON AuthAdapter and its Hilt binding (ExampleAuthModule): swap the backend here
```

Tests mirror the main source tree under `app/src/test/`.

## Layer rules

- `core/*` must never import `feature/*`.
- `feature/example` must stay deletable.
- Before writing a new composable, check `docs/architecture/components.md`; reuse or extend a component from `core/designsystem/component/`.
- Screens are split into a stateless `XxxScreen(state, callbacks)` and a thin `XxxRoute` that obtains the ViewModel via `hiltViewModel()`.
- State lives in ViewModels exposing `StateFlow`; preferences live in DataStore; Hilt wires singletons.
- Supported languages: `en` (fallback) and `id` (`res/values-in/`).
- Docs, comments, README, and AGENTS.md are written in English.

## Example routes and adding a page

Routes in `feature/example/ExampleRoutes.kt`: `ProfileRoute`, `AccountRoute` (protected), `SignInRoute`, `SettingsRoute`, `FormExampleRoute`, `ProductsRoute`, `ProductDetailRoute(id)`; the component pages `ActionsRoute`, `InputsRoute`, `DisplayRoute`, `OverlaysRoute`; and the styling pages `ColorsRoute`, `TypographyRoute`, `SpacingRoute`, `SizingRoute`, `LayoutRoute`, `AppearanceRoute`, `ThemeRoute`. `HomeRoute` is in `app/navigation/AppNavHost.kt`.

To add an example page:

1. Add a `@Serializable` route object to `ExampleRoutes.kt`.
2. Write a stateless `XxxScreen(..., onBack)` on top of `ExamplePageScaffold`.
3. Register `composable<XxxRoute>` in the right tab graph in `app/navigation/AppNavHost.kt` (`homeGraph`, `componentsGraph`, `profileGraph`).
4. Add an `ExampleEntry` to `exampleSections` (Home tab) or `componentEntries` (Components tab) in `ExampleCatalog.kt`, with title/subtitle strings in both `values/` and `values-in/`.
5. Cover it in a Robolectric test.

## Testing conventions

Robolectric tests use `@RunWith(AndroidJUnit4::class)` and `@Config(sdk = [36])` (Robolectric 4.17 does not yet support SDK 37).
Hilt-injected components use `@HiltAndroidTest` with `HiltAndroidRule` ordered before the Compose rule and `@Config(application = HiltTestApplication::class)`.

## Commands

```
./gradlew testDebugUnitTest   # unit and Robolectric tests
./gradlew ktlintCheck         # lint
./gradlew ktlintFormat        # auto-format
./gradlew assembleDebug       # build debug APK
```

Proof command for every change: `./gradlew testDebugUnitTest ktlintCheck assembleDebug`.
On Windows use `gradlew.bat`.

## Skills

Added in plan 6.
