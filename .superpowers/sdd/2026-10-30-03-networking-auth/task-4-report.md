# Task 4 report: DummyJSON adapter and sign-in

## Implemented
- `DummyJsonApi` (login @Anonymous, me), separate `DummyJsonRefreshApi` (refresh @Anonymous) provided from `@RefreshRetrofit` per the ruling; DTOs; `DemoTokenSettings`; `DummyJsonAuthAdapter(api, refreshApi, settings)`; real `ExampleAuthModule` (stub removed).
- `SignInViewModel` (extra `Json` ctor param, needed by `safeApiCall`), `validateSignIn`, `SignInScreen` (via `ExamplePageScaffold`), `SignInRoute`, `composable<SignInRoute>` in `profileGraph(navController)` (signedIn pops back until Task 5; `MainShell` call site updated).
- Strings en/id (14 keys). auth-session.md "Dependency cycle" bullet rewritten; SessionRefresher KDoc notes Errors propagate.

## TDD
- RED: `./gradlew testDebugUnitTest --tests "*DummyJsonAuthAdapterTest" --tests "*SignIn*"` -> `:app:compileDebugUnitTestKotlin FAILED`, `Unresolved reference 'DemoTokenSettings'`, `'DummyJsonApi'`, `Too many arguments for 'constructor(): DummyJsonAuthAdapter'` (types not yet existing).
- GREEN: `./gradlew testDebugUnitTest ktlintCheck assembleDebug` -> BUILD SUCCESSFUL; adapter 4/4, ViewModel 6/6, screen 4/4, all others passing.
- Adapter test builds two Retrofits on one MockWebServer with call-counting interceptors; refresh asserts refresh client count 1, app client count 0.

## Notes
- ViewModel takes `Json` in addition to the brief's params.
- ktlintFormat run before the final check.

## Fix round 1
- Added optional `onImeAction: (() -> Unit)?` to `AppTextField` and `PasswordTextField` (maps to the keyboard action matching `imeAction`); component test `ime_action_runs_callback_for_app_and_password_fields` in `TextFieldsTest`. SignInScreen password field: `onImeAction = onSubmit` (username stays `ImeAction.Next`); screen test `ime_done_on_password_submits`.
- `SignInViewModel`: session write, loading reset, success message and `signedIn` emit now run in `withContext(NonCancellable)`; emit remains after the store.
- New VM test `next_submit_clears_previous_error_while_loading`.
- Covering run: `./gradlew testDebugUnitTest --tests "*SignIn*" --tests "*TextFieldsTest"` -> BUILD SUCCESSFUL in 20s.
- Proof: `./gradlew testDebugUnitTest ktlintCheck assembleDebug` -> BUILD SUCCESSFUL in 21s.
