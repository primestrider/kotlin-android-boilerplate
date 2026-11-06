# Kotlin Starter

A Kotlin Android starter kit built with Jetpack Compose, Material 3, and Hilt. It is a port of a React Native boilerplate.

## Requirements

- Android Studio (latest stable)
- JDK 21 or newer (Robolectric runs the tests on Android SDK 36, which needs Java 21; the code still targets JVM 17)
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

See [AGENTS.md](AGENTS.md) for the project map, the module rules and conventions. New to the codebase (or to Android,
coming from React or Vue)? Start with the [learning guide](docs/learning/README.md).

## Project structure

Modular, in the style of Google's Now in Android:

- `app/`: the shell (activity, tabs, navigation host) and the only module that wires features together.
- `core/*`: shared building blocks (`common`, `model`, `datastore`, `designsystem`, `network`, `auth`, `ui`, `testing`).
- `feature/<name>/api` + `impl`: one pair per feature. The seven current features are examples.
- `build-logic/`: Gradle convention plugins shared by every module.

Module boundaries are Gradle dependencies, so a feature cannot reach into another feature's implementation. Details: [docs/architecture/overview.md](docs/architecture/overview.md).

## Starting a new project

No need to clone: download the code without its git history, rename it, optionally drop the examples, then start your
own repository.

1. **Get the code**: on GitHub, **Code → Download ZIP** and unzip it, or `npx degit primestrider/kotlin-android-boilerplate my-app`.
2. **Rename** the package/applicationId, app name, class and theme names, deep-link scheme and Gradle project name in one go:
   ```
   bash scripts/rename-project.sh com.acme.shop "Acme Shop"
   ```
3. **Remove the examples** (optional). This deletes every example feature and its wiring; the app still builds and runs, with a
   placeholder Home. `--keep .examples` first copies the removed code into a git-ignored `.examples/` folder that agents
   read as reference (see [AGENTS.md](AGENTS.md)); leave it out to delete it for good:
   ```
   bash scripts/remove-examples.sh --keep .examples
   ```
4. **Check and commit**:
   ```
   ./gradlew test assembleDevDebug
   git init && git add . && git commit -m "chore: start from kotlin-starter"
   ```

On Windows run the scripts from Git Bash, with Android Studio closed or Gradle stopped (`./gradlew --stop`): open
build files are locked and cannot be deleted. CI checks that the app still builds without the examples.

## Environments, release and CI

`dev` and `prod` flavours with an API URL each, release signing from environment variables or `local.properties`, R8 with resource shrinking, a baseline profile module, and a GitHub Actions workflow: see [docs/architecture/release.md](docs/architecture/release.md). Testing conventions: [docs/architecture/testing.md](docs/architecture/testing.md). Distribution to testers through Firebase App Distribution: [docs/firebase-app-distribution-setup.md](docs/firebase-app-distribution-setup.md).

## Styling and dark mode

- Design tokens live in `core/designsystem/theme/` (`Palette`, `AppColors`, `Spacing`, `Radii`, `Type`). Read semantic colors from `AppThemeTokens.colors` or `MaterialTheme.colorScheme`; never hardcode a `Color` in a screen.
- `AppTheme(darkTheme)` provides light or dark colors, Plus Jakarta Sans typography, and shapes. The user preference (System, Light, Dark) is persisted and applied before the first frame.
- The Home screen's Styling section opens seven showcase pages (Colors, Typography, Spacing, Sizing, Layout, Appearance, Theme) that demonstrate every token. The Theme page switches the mode live.
