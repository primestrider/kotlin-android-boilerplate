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
