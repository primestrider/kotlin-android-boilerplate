# Reusable components

`core/designsystem/component/` holds the components every feature can use. They are thin, stateless wrappers over Material 3: state and callbacks go in, UI comes out. Prefer one of these before writing a new composable; when a pattern repeats in two features, add it here.

Rules:

- Package `com.primestrider.kotlinstarter.core.designsystem.component`; never import `feature/*` or `app/*`.
- `modifier: Modifier = Modifier` is the first optional parameter.
- Colors only from `MaterialTheme.colorScheme` / `AppThemeTokens.colors`.
- Strings use the `component_` prefix, in `values/` and `values-in/`.
- Icon-only controls have a `contentDescription`; touch targets are at least 48dp.
- Form controls (text fields, checkbox, radio, switch, segmented, slider) always come from here, never from Material directly; `NoRawMaterialControlsTest` fails the build if a screen imports a stock Material control.
- Each component has a Robolectric test in `app/src/test/.../core/designsystem/component/`.

| Component | File | Use it for |
| --- | --- | --- |
| `AppScaffold`, `AppTopBar` | `AppScaffold.kt` | Every screen: title, optional back arrow (`onBack = null` hides it), actions, snackbar/FAB slots |
| `AppNavigationBar`, `NavBarItem` | `AppNavigationBar.kt` | Bottom navigation for top-level destinations, with optional count badge ("99+") |
| `AppButton`, `ButtonVariant` | `AppButton.kt` | Any button: `PRIMARY`, `SECONDARY` (tonal), `OUTLINED`, `TEXT`, `DESTRUCTIVE`; `loading = true` shows a spinner, reports disabled and ignores clicks (no double submit); `fullWidth`, `leadingIcon` |
| `AppTextField` | `TextFields.kt` | Full-width filled text input: no border, radius 16, floating label aligned with the value, tinted with `primary` while focused and with `destructive` in error. Placeholder, `errorText` (wins over `supportingText`), `maxLength` (counted in characters, emoji-safe) with an "n/max" counter, keyboard type, IME action, and `prefix` (a small chip before the value, e.g. `+62`). No decorative trailing icons |
| `PasswordTextField` | `TextFields.kt` | Password input with its own show/hide toggle |
| `SearchField` | `TextFields.kt` | Filled search box (placeholder, no label) with a clear button once there is a query |
| `SelectField`, `SelectOption` | `SelectField.kt` | Pick one value from a dropdown, same filled look as `AppTextField`; disabled options are shown but not selectable |
| `ConfirmDialog` | `Dialogs.kt` | Confirmation modal: `destructive` styles the confirm action, `dismissible = false` ignores back/outside taps, `dismissLabel = null` hides the dismiss button |
| `AppBottomSheet` | `BottomSheets.kt` | Modal bottom sheet with a title and any content; `dismissible = false` keeps it open on back, swipe and outside taps |
| `OptionsBottomSheet` | `BottomSheets.kt` | Single choice from a sheet (sort, filter); a tap hides the sheet, reports `onSelect` once, then `onDismiss` |
| `Messenger`, `UiMessage`, `UiText`, `MessageVariant` | `Messenger.kt` | App-wide snackbar messages (info / success / warning / error, optional action) from anywhere, see below |
| `MessengerHost` | `Messenger.kt` | Renders the messages; already placed in `MainShell`'s `snackbarHost` |
| `LoadingState`, `EmptyState`, `ErrorState` | `StateViews.kt` | Whole-area states of a screen or section: spinner, nothing-yet with optional action, failure with Retry |
| `LoadingOverlay` | `StateViews.kt` | Blocking spinner over its parent `Box` while a critical action runs; swallows touches and is announced only as "Loading" |
| `Banner`, `BannerVariant` | `Banner.kt` | Inline alert in the page (info / success / warning / error) with optional action and dismiss |
| `Avatar`, `AvatarGroup`, `AvatarSize`, `AvatarStatus`, `initialsOf` | `Avatar.kt` | Avatar (sizes 32/40/48/64dp, circle or custom shape, presence dot announced to TalkBack). Pass `imageUrl` to load a picture with Coil (`SubcomposeAsyncImage`, clipped to the shape); the initials show while it loads and if it fails. Overlapping group with "+N" overflow (initials only) |
| `Skeleton`, `SkeletonText` | `Skeleton.kt` | Pulsing placeholders while content loads; `SkeletonText` is announced as "Loading" |
| `SectionHeader` | `SectionHeader.kt` | Section title marked as an accessibility heading, with an optional "See all" style action |
| `InfoRow` | `InfoRow.kt` | Label / value pairs (order id, status, address); long values wrap |
| `AppCheckbox` | `Toggles.kt` | Borderless 22dp checkbox, two-state (`checked`) or tri-state (`ToggleableState`, indeterminate shows a dash) |
| `AppRadioButton` | `Toggles.kt` | Borderless 22dp radio |
| `AppSwitch` | `Toggles.kt` | iOS-style pill switch with a sliding white thumb |
| `SegmentedChoice` | `ChoiceControls.kt` | One of up to four short options: neutral track with a raised pill that slides to the selection; more options use `SelectField` |
| `MultiSegmentedChoice` | `ChoiceControls.kt` | Several of up to four options on the same track, each selected one raised |
| `SwitchRow` | `ChoiceControls.kt` | Label plus `AppSwitch` where the whole row toggles and reports the switch role |
| `LabeledSlider`, `LabeledRangeSlider` | `ChoiceControls.kt` | Thin slider (6dp track, white thumb) with its label and a value readout (`valueText`); the range version has two thumbs |

Checkbox, radio and switch follow Material's contract: pass a callback and the control is its own 48dp target with the right role; pass `null` inside a `toggleable` / `selectable` row and the row owns the click, so TalkBack announces it once.
| `InfoCard` | `InfoCard.kt` | Outlined card of `InfoRow`s from (label, value) pairs; null or blank values show an em dash |

## Showing messages

- From a composable: `val messenger = LocalMessenger.current` then `messenger.success(UiText.Resource(R.string.saved))`.
- From a ViewModel: inject `Messenger` (it is a Hilt `@Singleton`) and call the same methods; see `FeedbackViewModel`.
- Prefer `UiText.Resource` so the text follows the app language; `UiText.Plain` is for text that is already final (e.g. a server message).
- `UiMessage(text, variant, actionLabel, onAction, long)` adds an action ("Undo", "Retry"); `onAction` runs only if the user taps it.
- Messages are queued in a buffered channel (up to 64): one sent while no screen is collecting (rotation, before the UI starts) is shown once the host collects, and several are shown one after another in order. Keep `onAction` lambdas short-lived; they live until the message is shown.
- `MainActivity` provides `LocalMessenger`; tests that replace the activity content must provide it too (see `MainShellTest`).
