# AGENTS.md

Kotlin Android starter kit (Compose + Hilt), a port of a React Native boilerplate.

## Toolchain

AGP 9.4.0 (built-in Kotlin), Gradle 9.6.0, Kotlin 2.4.21, compileSdk/targetSdk 37, minSdk 24, JVM target 17.
All dependencies and plugins are declared in `gradle/libs.versions.toml`.

## Project map

Single module `:app`, root package `com.primestrider.kotlinstarter`.

```
KotlinStarterApp.kt          # @HiltAndroidApp
app/                         # MainActivity, AppViewModel, navigation (AppNavHost)
core/storage/                # DataStore module and preference keys
core/designsystem/theme/     # palette, semantic colors, spacing, radii, typography, AppTheme
core/i18n/                   # AppLanguage, LanguageController
feature/example/             # example catalog, Home, Settings, styling pages (deletable)
```

Tests mirror the main source tree under `app/src/test/`.

## Layer rules

- `core/*` must never import `feature/*`.
- `feature/example` must stay deletable.
- Screens are split into a stateless `XxxScreen(state, callbacks)` and a thin `XxxRoute` that obtains the ViewModel via `hiltViewModel()`.
- State lives in ViewModels exposing `StateFlow`; preferences live in DataStore; Hilt wires singletons.
- Supported languages: `en` (fallback) and `id` (`res/values-in/`).
- Docs, comments, README, and AGENTS.md are written in English.

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
