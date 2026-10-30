# Kotlin Starter

A Kotlin Android starter kit built with Jetpack Compose, Material 3, and Hilt. It is a port of a React Native boilerplate.

## Requirements

- Android Studio (latest stable)
- JDK 17 or newer
- Android SDK 37

## Run

```
./gradlew assembleDebug       # build
./gradlew installDebug        # install on a connected device or emulator
./gradlew testDebugUnitTest   # unit tests
./gradlew ktlintCheck         # lint
./gradlew ktlintFormat        # auto-format
```

On Windows use `gradlew.bat`.

See [AGENTS.md](AGENTS.md) for the project map and conventions.

## Styling and dark mode

- Design tokens live in `core/designsystem/theme/` (`Palette`, `AppColors`, `Spacing`, `Radii`, `Type`). Read semantic colors from `AppThemeTokens.colors` or `MaterialTheme.colorScheme`; never hardcode a `Color` in a screen.
- `AppTheme(darkTheme)` provides light or dark colors, Plus Jakarta Sans typography, and shapes. The user preference (System, Light, Dark) is persisted and applied before the first frame.
- The Home screen's Styling section opens seven showcase pages (Colors, Typography, Spacing, Sizing, Layout, Appearance, Theme) that demonstrate every token. The Theme page switches the mode live.
