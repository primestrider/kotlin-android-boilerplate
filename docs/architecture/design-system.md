# Design system

Tokens and theme live in `app/src/main/java/com/primestrider/kotlinstarter/core/designsystem/theme/`.

## Token files

| File | Contents |
| --- | --- |
| `Palette.kt` | Raw colors: `white`, `black`, every scale (`Palette.Gray.c50` ... `c950`, `Primary`, `Taupe`, `Mauve`, `Mist`, `Olive`, semantic `Success`/`Warning`/`Error`/`Info`) and `Palette.scales` for the Colors showcase page. |
| `AppColors.kt` | `AppColors` semantic roles, `LightColors`, `DarkColors`, `LocalAppColors`. |
| `Spacing.kt` | `Spacing.s0` ... `s96` (4dp based, `s0_5` = 2dp). |
| `Radii.kt` | `Radii.none` ... `full` (`DEFAULT` in RN equals `md`). |
| `Type.kt` | `PlusJakartaSans`, `FontSize`, `LineHeight`, `LetterSpacing`, `AppTypography`. |
| `AppTheme.kt` | `AppTheme(darkTheme)` and `AppThemeTokens`. |

## Reading colors

- `AppThemeTokens.colors` gives the full semantic set (`AppColors`), including `success`, `warning` and `info`, which Material 3 has no slot for.
- `MaterialTheme.colorScheme` is what Material components read. Use it when styling with Material roles.

Never hardcode a `Color` in a screen. Use `AppThemeTokens.colors`, `MaterialTheme.colorScheme`, or (for showcase pages only) `Palette`.

## Material 3 color mapping

| ColorScheme | AppColors |
| --- | --- |
| primary / onPrimary | primary / primaryForeground |
| background / onBackground | background / foreground |
| surface / onSurface | card / cardForeground |
| surfaceVariant / onSurfaceVariant | secondary / muted |
| secondaryContainer / onSecondaryContainer | secondary / secondaryForeground |
| error / onError | destructive / destructiveForeground |
| outline, outlineVariant | border |

Shapes: small = `Radii.sm`, medium = `Radii.md`, large = `Radii.lg`, extraLarge = `Radii.xl`.

## Typography mapping

| Material 3 | RN variant | Size / line height | Weight |
| --- | --- | --- | --- |
| headlineLarge | h1 | xl3 | ExtraBold, tight spacing |
| headlineMedium | h2 | xl2 | Bold, tight spacing |
| headlineSmall | h3 | xl | SemiBold |
| titleLarge / titleMedium / titleSmall | title | lg / base / sm | SemiBold |
| bodyLarge / bodyMedium | body | base / sm | Normal |
| bodySmall | caption | xs | Normal |
| labelLarge | label | sm | Medium |
| labelMedium / labelSmall | | xs | Medium |
| displayLarge / Medium / Small | | xl6 / xl5 / xl4 | Bold |

All styles use Plus Jakarta Sans (400 to 800).

## Theme preference

- The mode (`SYSTEM`, `LIGHT`, `DARK`) is stored in the `settings` Preferences DataStore under the key `app.theme` (`StorageKeys.themeMode`), via `ThemePreferenceRepository`. Unknown or missing values resolve to `SYSTEM`.
- `AppViewModel.themeMode` is `null` until the first stored value arrives. `MainActivity` keeps the splash screen visible while it is `null`, so the first frame is drawn in the correct theme (no flash of the wrong theme on cold start).
- If reading DataStore throws an `IOException`, the repository falls back to `SYSTEM` instead of crashing.
- System bar icon colors follow the resolved theme: `enableEdgeToEdge(SystemBarStyle.auto(...))` is re-applied whenever it changes.

## Showcase pages

`feature/example/styling/` holds seven pages (Colors, Typography, Spacing, Sizing, Layout, Appearance, Theme) built on `StylingScaffold`. They read tokens from `AppThemeTokens`, `MaterialTheme`, `Spacing`, `Radii`, `FontSize`, and `Palette` (swatches only). The Theme page reuses `ThemeModeSelector`, the same System / Light / Dark control as Settings.

RN's `helpers` page (`view()`, `space()`, `gridCol()`) is intentionally not ported: those are React Native style-object utilities with no Compose counterpart (use `Modifier`, `Arrangement`, and `LazyVerticalGrid` directly).
