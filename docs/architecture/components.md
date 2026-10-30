# Reusable components

`core/designsystem/component/` holds the components every feature can use. They are thin, stateless wrappers over Material 3: state and callbacks go in, UI comes out. Prefer one of these before writing a new composable; when a pattern repeats in two features, add it here.

Rules:

- Package `com.primestrider.kotlinstarter.core.designsystem.component`; never import `feature/*` or `app/*`.
- `modifier: Modifier = Modifier` is the first optional parameter.
- Colors only from `MaterialTheme.colorScheme` / `AppThemeTokens.colors`.
- Strings use the `component_` prefix, in `values/` and `values-in/`.
- Icon-only controls have a `contentDescription`; touch targets are at least 48dp.
- Each component has a Robolectric test in `app/src/test/.../core/designsystem/component/`.

| Component | File | Use it for |
| --- | --- | --- |
| `AppScaffold`, `AppTopBar` | `AppScaffold.kt` | Every screen: title, optional back arrow (`onBack = null` hides it), actions, snackbar/FAB slots |
| `AppNavigationBar`, `NavBarItem` | `AppNavigationBar.kt` | Bottom navigation for top-level destinations, with optional count badge ("99+") |
| `AppButton`, `ButtonVariant` | `AppButton.kt` | Any button: `PRIMARY`, `SECONDARY` (tonal), `OUTLINED`, `TEXT`, `DESTRUCTIVE`; `loading = true` shows a spinner, reports disabled and ignores clicks (no double submit); `fullWidth`, `leadingIcon` |
| `AppTextField` | `TextFields.kt` | Text input with label, placeholder, `errorText` (wins over `supportingText`), `maxLength` + counter, keyboard type and IME action |
| `PasswordTextField` | `TextFields.kt` | Password input with its own show/hide toggle |
| `SearchField` | `TextFields.kt` | Search box with a clear button once there is a query |
| `SelectField`, `SelectOption` | `SelectField.kt` | Pick one value from a dropdown; disabled options are shown but not selectable |
| `ConfirmDialog` | `Dialogs.kt` | Confirmation modal: `destructive` styles the confirm action, `dismissible = false` ignores back/outside taps, `dismissLabel = null` hides the dismiss button |
| `AppBottomSheet` | `BottomSheets.kt` | Modal bottom sheet with a title and any content; `dismissible = false` keeps it open on back, swipe and outside taps |
| `OptionsBottomSheet` | `BottomSheets.kt` | Single choice from a sheet (sort, filter); a tap hides the sheet, reports `onSelect` once, then `onDismiss` |
| `Messenger`, `UiMessage`, `UiText`, `MessageVariant` | `Messenger.kt` | App-wide snackbar messages (info / success / warning / error, optional action) from anywhere, see below |
| `MessengerHost` | `Messenger.kt` | Renders the messages; already placed in `MainShell`'s `snackbarHost` |
| `LoadingState`, `EmptyState`, `ErrorState` | `StateViews.kt` | Whole-area states of a screen or section: spinner, nothing-yet with optional action, failure with Retry |
| `LoadingOverlay` | `StateViews.kt` | Blocking spinner over its parent `Box` while a critical action runs; swallows touches |
| `Banner`, `BannerVariant` | `Banner.kt` | Inline alert in the page (info / success / warning / error) with optional action and dismiss |

## Showing messages

- From a composable: `val messenger = LocalMessenger.current` then `messenger.success(UiText.Resource(R.string.saved))`.
- From a ViewModel: inject `Messenger` (it is a Hilt `@Singleton`) and call the same methods; see `FeedbackViewModel`.
- Prefer `UiText.Resource` so the text follows the app language; `UiText.Plain` is for text that is already final (e.g. a server message).
- `UiMessage(text, variant, actionLabel, onAction, long)` adds an action ("Undo", "Retry"); `onAction` runs only if the user taps it.
- Messages are queued in a buffered channel: one sent while no screen is collecting (rotation, before the UI starts) is shown once the host collects. A newer message replaces the one on screen.
- `MainActivity` provides `LocalMessenger`; tests that replace the activity content must provide it too (see `MainShellTest`).
