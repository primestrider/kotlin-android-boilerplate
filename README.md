# Kotlin Starter

A Kotlin Android starter kit built with Jetpack Compose, Material 3, and Hilt. It is a port of a React Native boilerplate.

## Requirements

- Android Studio (latest stable)
- JDK 17 or newer
- Android SDK 37

## Run

```
./gradlew assembleDevDebug     # build the dev flavour
./gradlew installDevDebug      # install it on a connected device or emulator
./gradlew test                 # unit + Robolectric tests of every module
./gradlew ktlintCheck lint     # ktlint and Android Lint
./gradlew ktlintFormat         # auto-format
```

On Windows use `gradlew.bat`.

See [AGENTS.md](AGENTS.md) for the project map, the module rules and conventions.

## Project structure

Modular, in the style of Google's Now in Android:

- `app/`: the shell (activity, tabs, navigation host) and the only module that wires features together.
- `core/*`: shared building blocks (`common`, `model`, `datastore`, `designsystem`, `network`, `auth`, `ui`, `testing`).
- `feature/<name>/api` + `impl`: one pair per feature. The seven current features are examples.
- `build-logic/`: Gradle convention plugins shared by every module.

Module boundaries are Gradle dependencies, so a feature cannot reach into another feature's implementation. Details: [docs/architecture/overview.md](docs/architecture/overview.md).

## Removing the examples

`bash scripts/remove-examples.sh <copy-of-the-project>` deletes every example feature and its wiring; the app still builds and runs, with a placeholder Home. Try it on a copy (`git worktree add`) first. CI checks that this keeps working.

## Environments, release and CI

`dev` and `prod` flavours with an API URL each, release signing from environment variables or `local.properties`, R8 with resource shrinking, a baseline profile module, and a GitHub Actions workflow: see [docs/architecture/release.md](docs/architecture/release.md). Testing conventions: [docs/architecture/testing.md](docs/architecture/testing.md). Distribution to testers through Firebase App Distribution: [docs/firebase-app-distribution-setup.md](docs/firebase-app-distribution-setup.md).

## Styling and dark mode

- Design tokens live in `core/designsystem/theme/` (`Palette`, `AppColors`, `Spacing`, `Radii`, `Type`). Read semantic colors from `AppThemeTokens.colors` or `MaterialTheme.colorScheme`; never hardcode a `Color` in a screen.
- `AppTheme(darkTheme)` provides light or dark colors, Plus Jakarta Sans typography, and shapes. The user preference (System, Light, Dark) is persisted and applied before the first frame.
- The Home screen's Styling section opens seven showcase pages (Colors, Typography, Spacing, Sizing, Layout, Appearance, Theme) that demonstrate every token. The Theme page switches the mode live.
