# Examples

Everything under `feature/example/` is demo code and can be deleted without touching `core/*`. This page covers the "Device APIs" Home section (`feature/example/sdk/`): small, real Android screens that exercise one platform API each.

## The Device APIs section

The section sits on the Home tab between Features and Styling. Its entries come from one registry, `sdkDemos` in `feature/example/sdk/SdkDemos.kt`:

```kotlin
SdkDemo("clipboard", R.string.sdk_clipboard_title, R.string.sdk_clipboard_subtitle) { ClipboardScreen(it) }
```

All demos share one navigation destination, `SdkDemoRoute(id)`. `SdkDemoScreen` looks the id up in the registry and shows the demo; an unknown id shows an empty state with a back arrow. The Home section and the `homeGraph` entry are built from the registry, so a demo never needs its own route.

### Adding a demo

1. Write a stateless `XxxScreen(onBack: () -> Unit)` in `feature/example/sdk/<name>/` on top of `ExamplePageScaffold`; use `SdkSection` (a `DemoSection` plus a muted description) and `InfoCard` for label/value data.
2. Add `sdk_<name>_` strings to `values/strings.xml` and `values-in/strings.xml` (`TranslationCompletenessTest` enforces both).
3. Append a `SdkDemo("kebab-id", title, subtitle) { XxxScreen(it) }` entry to `sdkDemos`. The order of the list is the order on Home.
4. Declare any permission or `<queries>` entry in `AndroidManifest.xml`, and test the demo's logic (pure functions or a ViewModel); add a Robolectric test where the UI has logic-driven states.

### PermissionGate

`core/permission/PermissionGate` wraps content that needs runtime permissions:

```kotlin
PermissionGate(permissions = listOf(Manifest.permission.CAMERA), reason = "The camera scans the code.") { Scanner() }
```

It shows the content once **all** listed permissions are granted. Until then it shows an outlined card with the reason and a "Grant access" button. When the system will no longer show the prompt (the permission was requested before and no rationale is offered, see `permissionStatus`), the card says "Enable it in Settings to continue." and the button opens the app's settings page. Which permissions were requested is stored in `SharedPreferences("permissions")` so "blocked" survives a restart, and the state is re-read on every `ON_RESUME`, so granting access in Settings and coming back shows the content.

Use it for any demo that needs a runtime permission, instead of requesting permissions by hand.

### Deleting the section

Delete `feature/example/sdk/` and `SdkDemoRoute` in `ExampleRoutes.kt`; remove the "Device APIs" `ExampleSection` from `ExampleCatalog.kt`, the `composable<SdkDemoRoute>` line in `AppNavHost.kt`, the `sdk_*` and `example_home_sdk_*` strings, and what the demos added to the manifest and `libs.versions.toml` (`ACCESS_NETWORK_STATE`, the `review-ktx` and `exifinterface` dependencies, `room-testing`). The Share & Print demo also added the `<provider>` for `androidx.core.content.FileProvider` (authority `${applicationId}.files`) to `AndroidManifest.xml` and `res/xml/file_paths.xml`: delete both, and any `cache-path` entries later demos added to that file. The Room transactions demo put its table in `ExampleDatabase`: drop `TransactionEntity` and `transactionDao()` from it, remove `MIGRATION_1_2` and the `.addMigrations(...)` call and the `transactionDao` provider in `TodosModule`, and set `version = 1` again (or keep the migration if any installed build is on v2). The debug-only `app/schemas` asset directory in `app/build.gradle.kts` exists for the migration test and can go with it. `core/permission/` and the shared controls in `core/designsystem/component/` can stay: they do not depend on the demos.

## Demos

### Clipboard & Haptics (`clipboard`)

- **Purpose:** copy a referral code, paste the clipboard into a field, and try the haptic effects.
- **Android API:** `ClipboardManager` (`setPrimaryClip`, `primaryClip`, `OnPrimaryClipChangedListener`), `View.performHapticFeedback`.
- **Permissions:** none.
- **API-level fallback:** the success and error haptics use `CONFIRM` and `REJECT` on API 30+, and `KEYBOARD_TAP` and `LONG_PRESS` below.
- **Files:** `sdk/clipboard/ClipboardScreen.kt`.

### Device & Network (`device`)

- **Purpose:** live connectivity (type, connected, internet reachable) and build constants (brand, model, OS, memory, performance class).
- **Android API:** `ConnectivityManager.registerDefaultNetworkCallback`, `NetworkCapabilities`, `Build`, `ActivityManager.MemoryInfo`, `UiModeManager`, `Settings.Global.AIRPLANE_MODE_ON`.
- **Permissions:** `ACCESS_NETWORK_STATE` (normal, granted at install).
- **API-level fallback:** "Performance class" needs API 31 (`Build.VERSION.MEDIA_PERFORMANCE_CLASS`); older devices, and devices that declare none, show "—".
- **Files:** `sdk/device/DeviceScreen.kt`, `sdk/device/NetworkStatus.kt`.

### Share & Print (`share`)

- **Purpose:** render an e-receipt (merchant, amount) into a one-page A4 PDF, then share it through the system sheet or print it.
- **Android API:** `PdfDocument`, `FileProvider` (authority `${applicationId}.files`), `Intent.ACTION_SEND` with `Intent.createChooser`, `PrintManager.print` with a `PrintDocumentAdapter`.
- **Permissions:** none.
- **API-level fallback:** none needed; all of it exists on API 24. If no app can handle the share (`ActivityNotFoundException`) or printing fails, the screen shows "Something went wrong."
- **Files:** `sdk/share/ShareScreen.kt`, `sdk/share/Receipt.kt` (amount parsing, reference), `sdk/share/ReceiptPdf.kt` (page content, PDF writer, print adapter), `res/xml/file_paths.xml`, the `<provider>` in `AndroidManifest.xml`. Robolectric has no native PDF backend, so only the page content is unit tested; the rendering is checked on a device.

### Room transactions (`sqlite`)

- **Purpose:** an offline transaction history. Pick a kind (top-up, payment, transfer) and an amount, add it, and watch the balance; it survives restarts.
- **Android API:** Room (`@Entity`, `@Dao` with `Flow`, a `Migration`).
- **Permissions:** none.
- **API-level fallback:** not applicable.
- **Files:** `sdk/sqlite/` (`TransactionEntity`, `TransactionDao`, `TransactionsViewModel`, `TransactionsScreen`). The table lives in `ExampleDatabase` (`todos/ExampleDatabase.kt`, version 2, with `MIGRATION_1_2`) and the DAO is provided by `TodosModule`. The upgrade is covered by `ExampleDatabaseMigrationTest`; see "Adding a table" in [storage-database.md](architecture/storage-database.md).

### Image Compression & Files (`files`)

- **Purpose:** pick a photo, resize it to a maximum width and re-encode it as JPEG (shows the saving); download a sample PDF into the cache directory and list or clear it.
- **Android API:** Photo Picker (`ActivityResultContracts.PickVisualMedia`), `BitmapFactory` (`inSampleSize`), `ExifInterface` (rotation), `Bitmap.createScaledBitmap`, OkHttp.
- **Permissions:** none. The Photo Picker needs no storage permission.
- **API-level fallback:** the Photo Picker falls back to the system document picker on devices without it (down to API 24).
- **Files:** `sdk/files/` (`ImageCompressor`, `Downloads`, `FilesViewModel`, `FilesScreen`); the `exifinterface` dependency in `libs.versions.toml`.

### App Info & Store Review (`app-info`)

- **Purpose:** show the installed package's info and request the Play in-app review sheet, with the store page as a fallback.
- **Android API:** `PackageManager.getPackageInfo`, `PackageInfoCompat.getLongVersionCode`, Play Core `ReviewManager` (`review-ktx`), `market://` and `https://play.google.com` intents.
- **Permissions:** none.
- **API-level fallback:** `getPackageInfo` uses `PackageInfoFlags` on API 33+ and the older overload below. The review flow needs Google Play; without it the screen says "In-app review is not available here."
- **Files:** `sdk/appinfo/AppInfoScreen.kt`.

### Live Photo (`live-photo`)

- **Purpose:** Live Photos are an iOS feature; the page explains that instead of faking it.
- **Android API:** none.
- **Permissions:** none.
- **API-level fallback:** not applicable.
- **Files:** `sdk/livephoto/LivePhotoScreen.kt`.

### Binary data (`blob`)

- **Purpose:** the counterpart of a JavaScript `Blob`: create a byte payload from text, read it back as text, slice its first 8 bytes as hex, or combine it with itself.
- **Android API:** none; plain Kotlin (`ByteArray`, UTF-8).
- **Permissions:** none.
- **API-level fallback:** not applicable.
- **Files:** `sdk/blob/BinaryPayload.kt`, `sdk/blob/BlobScreen.kt`.

### System Bars (`status-bar`)

- **Purpose:** change the status bar icon style (Auto, Light icons, Dark icons, Inverted) and hide it, for this screen only.
- **Android API:** `WindowCompat.getInsetsController` (`isAppearanceLightStatusBars`, `hide`/`show` of `WindowInsetsCompat.Type.statusBars()`).
- **Permissions:** none.
- **API-level fallback:** none needed; the compat controller covers API 24 and up. Leaving the screen restores the previous icon style and visibility.
- **Files:** `sdk/statusbar/StatusBarScreen.kt`, `sdk/statusbar/SystemBarStyle.kt` (the style-to-appearance rule).
