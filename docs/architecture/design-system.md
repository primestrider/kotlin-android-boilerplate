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
