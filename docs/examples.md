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

It shows the content once **all** listed permissions are granted (`requireAll = false`: once any one is; the prompt still asks for all, as location needs). Until then it shows an outlined card with the reason and a "Grant access" button. When the system will no longer show the prompt (the permission was requested before and no rationale is offered, see `permissionStatus`), the card says "Enable it in Settings to continue." and the button opens the app's settings page. Which permissions were requested is stored in `SharedPreferences("permissions")` so "blocked" survives a restart, and the state is re-read on every `ON_RESUME`, so granting access in Settings and coming back shows the content.

Use it for any demo that needs a runtime permission, instead of requesting permissions by hand.

### Deleting the section

Delete `feature/example/sdk/` and `SdkDemoRoute` in `ExampleRoutes.kt`; remove the "Device APIs" `ExampleSection` from `ExampleCatalog.kt`, the `composable<SdkDemoRoute>` line in `AppNavHost.kt`, the `sdk_*` and `example_home_sdk_*` strings, and what the demos added to the manifest and `libs.versions.toml` (`ACCESS_NETWORK_STATE`, the `review-ktx` and `exifinterface` dependencies, `room-testing`). The Share & Print demo also added the `<provider>` for `androidx.core.content.FileProvider` (authority `${applicationId}.files`) to `AndroidManifest.xml` and `res/xml/file_paths.xml`: delete both, and any `cache-path` entries later demos added to that file. The Room transactions demo put its table in `ExampleDatabase`: drop `TransactionEntity` and `transactionDao()` from it, remove `MIGRATION_1_2` and the `.addMigrations(...)` call and the `transactionDao` provider in `TodosModule`, and set `version = 1` again (or keep the migration if any installed build is on v2). The debug-only `app/schemas` asset directory in `app/build.gradle.kts` exists for the migration test and can go with it. The Web Browser and Payment Code demos added more outside `feature/example`: remove from `AndroidManifest.xml` the whole `<queries>` block, the `DETECT_SCREEN_CAPTURE` permission, the `android:launchMode="singleTop"` attribute and the `<intent-filter>` with `${appScheme}` on `MainActivity`; from `app/build.gradle.kts` the `appScheme` value, `manifestPlaceholders["appScheme"]`, the `APP_SCHEME` `buildConfigField` and the `androidx.browser` dependency (and `browser` / `androidx-browser` in `libs.versions.toml`); in `AppNavHost.kt` the `deepLinks = ...` argument of `composable<SdkDemoRoute>` (with the `navDeepLink` and `BuildConfig` imports). `MainActivity` keeps its `addOnNewIntentListener` hook, which is harmless without deep links, or you can drop it together with the hoisted `rememberNavController()` and let `MainShell` create the controller again. The Notifications, Calendar, Contacts and Location demos added more outside `feature/example`: remove from `AndroidManifest.xml` the `POST_NOTIFICATIONS`, `READ_CALENDAR`, `WRITE_CALENDAR`, `READ_CONTACTS`, `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` permissions and the `<receiver>` for `NotificationReceiver`; delete `res/drawable/sdk_ic_notification.xml`; from `app/build.gradle.kts` the `play.services.location` and `androidx.lifecycle.process` dependencies (and `playServicesLocation`, `play-services-location`, `androidx-lifecycle-process` in `libs.versions.toml`). The Camera and Image Picker demos added more outside `feature/example`: remove from `AndroidManifest.xml` the `CAMERA` permission and the `<uses-feature android:name="android.hardware.camera" .../>` line; delete the `<cache-path name="camera" .../>` entry from `res/xml/file_paths.xml` (delete the file with the provider if nothing else uses it); from `app/build.gradle.kts` the `androidx.camera.camera2`, `androidx.camera.lifecycle`, `androidx.camera.view` and `mlkit.barcode.scanning` dependencies (and `cameraX`, `mlkitBarcode`, `androidx-camera-*`, `mlkit-barcode-scanning` in `libs.versions.toml`). `core/permission/` and the shared controls in `core/designsystem/component/` can stay: they do not depend on the demos.

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

### Blur & Gradients (`effects`)

- **Purpose:** a wallet card with a linear gradient at any angle (three palettes), a balance hidden behind a blur with an intensity slider, and a 3×3 mesh gradient whose middle point and smoothing you can change.
- **Android API:** `Brush.linearGradient`, `Modifier.blur` (API 31+), an AGSL `RuntimeShader` drawn with `ShaderBrush` (API 33+).
- **Permissions:** none.
- **API-level fallback:** below API 31 there is no blur: a translucent scrim covers the balance and the caption "Blur needs Android 12; showing a scrim instead." shows. Below API 33 the mesh is a plain linear gradient with the caption "Mesh gradients need Android 13; showing a linear gradient instead."
- **Files:** `sdk/effects/GradientMath.kt` (angle to offsets, intensity to blur radius), `sdk/effects/MeshGradient.kt` (the shader), `sdk/effects/EffectsScreen.kt`.

### Web Browser & Linking (`web-browser`)

- **Purpose:** open a page in a Custom Tab themed with the app colors, hand `https` / `tel:` / `mailto:` / `geo:` URIs to other apps, and show the deep link that opens Payment Code from outside the app.
- **Android API:** `androidx.browser` (`CustomTabsIntent`, `CustomTabColorSchemeParams`), `Intent.ACTION_VIEW`, a manifest deep link.
- **Permissions:** none. Android 11+ package visibility needs the `<queries>` entries in the manifest.
- **API-level fallback:** not applicable. `ActivityNotFoundException` is caught everywhere: a missing handler shows a warning banner, and a Custom Tab with no Custom Tabs browser falls back to a plain `ACTION_VIEW`.
- **Files:** `sdk/browser/ExternalApps.kt` (`ExternalApp`, `openExternal`, `openCustomTab`, `paymentDeepLink`), `sdk/browser/WebBrowserScreen.kt`.
- **Try the link:** `adb shell am start -a android.intent.action.VIEW -d "kotlinstarter://example/sdk/payment-code?amount=50000"` (the scheme is `appScheme`, see below).

### Payment Code (`payment-code`)

- **Purpose:** a payment code (3 groups of 4 digits) that refreshes every 60 s, on a screen that is at full brightness, stays awake and blocks screenshots while it is open. The deep link `.../sdk/payment-code?amount=50000` also shows the amount.
- **Android API:** `Window.attributes.screenBrightness`, `FLAG_KEEP_SCREEN_ON`, `FLAG_SECURE`, `Activity.registerScreenCaptureCallback` (API 34+).
- **Permissions:** `DETECT_SCREEN_CAPTURE` (normal, granted at install).
- **API-level fallback:** screenshot detection needs API 34; below it the caption "Screenshot detection needs Android 14." shows. The three window treatments work on every API level.
- **Behavior:** `WindowTreatments` restores only what it changed: a flag the window already had stays set, and brightness returns to its earlier value, when a switch goes off or the screen closes. The amount reaches `PaymentCodeViewModel` through the navigation `SavedStateHandle` (`amount` argument of `SdkDemoRoute`).
- **Files:** `sdk/payment/PaymentCode.kt`, `PaymentCodeViewModel.kt`, `WindowTreatments.kt`, `PaymentCodeScreen.kt`.

### Location (`location`)

- **Purpose:** the current position once or live (every 5 s, 5 m minimum distance) at low, balanced or high accuracy, with latitude, longitude, accuracy and time; a one-off fix is reverse-geocoded into street, district, city, region, postal code and country.
- **Android API:** `FusedLocationProviderClient` (`getCurrentLocation`, `requestLocationUpdates` inside a `DisposableEffect` keyed on the watch switch and the accuracy), `Geocoder`.
- **Permissions:** `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`, requested together through `PermissionGate(requireAll = false)`: either one opens the demo. With approximate access only, High accuracy runs at balanced power (`effectivePriority`). A fix that does not arrive within 15 s shows "Could not get a location."
- **API-level fallback:** `Geocoder.GeocodeListener` needs API 33; below it `getFromLocation` runs on `Dispatchers.IO`. When `Geocoder.isPresent()` is false the address rows show "—" and the caption "No geocoder on this device." shows.
- **Files:** `sdk/location/LocationFormat.kt` (`formatCoordinate`, `formatAccuracy`), `sdk/location/LocationScreen.kt`.

### Notifications (`notifications`)

- **Purpose:** post a local notification now or after a delay (5 s, 30 s, 1 min), choose whether it shows while the app is open, cancel everything scheduled, and see a log of the last five events (scheduled, received, tapped). Tapping a notification opens this demo through its deep link.
- **Android API:** `NotificationManagerCompat`, a `NotificationChannel` with id `default`, `AlarmManager.set(ELAPSED_REALTIME_WAKEUP, ...)` (inexact, so no exact-alarm permission), a `BroadcastReceiver`, `ProcessLifecycleOwner` (to know whether the app is open).
- **Permissions:** `POST_NOTIFICATIONS` (runtime permission from Android 13).
- **API-level fallback:** below Android 13 there is nothing to ask for; the gate passes and the status row reads "granted (not needed before Android 13)". The channel is only created from Android 8.
- **Layout:** the Status row (granted / denied / not requested), the form and the Events log show before the grant; a `PermissionGate` under the Status row asks for access and "Schedule" stays disabled until it is granted.
- **Behavior:** the ids of the scheduled notifications are kept in `SharedPreferences("sdk_notifications")`, so the "n pending" count survives rotation and process death; an id whose alarm is gone (device restart) is dropped. The "Show while app is open" switch lives in the same preferences, because `NotificationReceiver` reads it. The title, the delay and the log are in the ViewModel `SavedStateHandle` under `notifications_*` keys; a tap makes navigation rebuild the screen, so the log is also carried to the next ViewModel through `NotificationEvents.history`.
- **Files:** `sdk/notifications/NotificationScheduler.kt`, `NotificationReceiver.kt`, `NotificationEvents.kt`, `NotificationsViewModel.kt`, `NotificationsScreen.kt`, and `res/drawable/sdk_ic_notification.xml` (the small icon).

### Contacts (`contacts`)

- **Purpose:** the system contact picker (name and first phone number of the one contact the user shares), and a search of the address book by name (250 ms debounce, 10 rows) once `READ_CONTACTS` is granted.
- **Android API:** `ActivityResultContracts.PickContact`, `ContactsContract`. All queries run on `Dispatchers.IO` in `ContactsRepository`.
- **Permissions:** none for the picker; `READ_CONTACTS` for the search section only (`PermissionGate` around that section).
- **API-level fallback:** not applicable. A missing contacts app (`ActivityNotFoundException`) shows "Contact picker failed."
- **Files:** `sdk/contacts/ContactsRepository.kt` (also `ContactsSource`, `ContactRow`), `ContactsViewModel.kt` (query in `SavedStateHandle` under `contacts_query`), `ContactsScreen.kt`.

### Calendar (`calendar`)

- **Purpose:** add one of three preset bookings (flight, doctor, movie) at 09:00 tomorrow, in 3 days or next week to the first writable calendar, optionally with a reminder an hour before, and list the events of the next 7 days.
- **Android API:** `CalendarContract` (`Calendars`, `Events`, `Reminders`, `Instances`) through `CalendarRepository`, on `Dispatchers.IO`.
- **Permissions:** `READ_CALENDAR` and `WRITE_CALENDAR`, as a gate around the whole screen.
- **API-level fallback:** none needed. `bookingStart` uses `java.util.Calendar`, because `java.time` needs API 26. A device without a writable calendar shows "No writable calendar on this device."
- **Files:** `sdk/calendar/Bookings.kt` (presets, `bookingStart`, `bookingEnd`), `CalendarRepository.kt`, `CalendarScreen.kt`.

### Camera & QR Scanner (`camera`)

- **Purpose:** a CameraX preview that scans QR codes (the value opens in a sheet) or takes photos, with flash, torch, zoom and lens flip.
- **Android API:** CameraX (`Preview`, `ImageCapture` at JPEG quality 70, `ImageAnalysis` with `STRATEGY_KEEP_ONLY_LATEST`, `PreviewView`), ML Kit barcode scanning (QR only, bundled model).
- **Permissions:** `CAMERA`, asked by a whole-screen `PermissionGate`; `<uses-feature android:name="android.hardware.camera" android:required="false"/>` keeps the app installable on devices without a camera.
- **API-level fallback:** none needed; CameraX supports API 24.
- **Files:** `sdk/camera/` (`QrResultGate`, `QrAnalyzer`, `CameraController`, `CameraScreen`). `QrResultGate` is held in a ViewModel and accepts one value at a time until the sheet is closed, so a code the camera keeps seeing opens one sheet. Frames are analysed on a single-thread executor; `CameraController.release()` unbinds the camera, closes the detector and shuts that executor down. Photos go to `cacheDir/camera/` (the `cache-path` entry in `res/xml/file_paths.xml`). Only the gate is unit tested, plus the result sheet; the camera is checked on a device.

### Image & Document Picker (`image-picker`)

- **Purpose:** pick images or videos from the Photo Picker (one or many) or take a photo, re-encode images as JPEG at a chosen quality, optionally crop a single image to a centered square; pick documents filtered by MIME type.
- **Android API:** `PickVisualMedia` / `PickMultipleVisualMedia`, `TakePicture` into a `FileProvider` URI (authority `${applicationId}.files`), `OpenMultipleDocuments`, `ContentResolver`, `BitmapFactory` (`inJustDecodeBounds`), `MediaMetadataRetriever`, `ImageCompressor`.
- **Permissions:** none for the Photo Picker and documents. `CAMERA` is requested only when "Camera" is pressed; a denial shows "Camera permission was denied."
- **API-level fallback:** the Photo Picker falls back to the system document picker on devices without it (down to API 24); a launch with no matching app (`ActivityNotFoundException`) shows an error.
- **Files:** `sdk/picker/` (`PickedMedia`, `SquareCrop`, `PickerViewModel`, `ImagePickerScreen`); results are written to `cacheDir/picker/`.

### Deep link scheme

The scheme is `appScheme`: `app.scheme` in `local.properties`, else the `APP_SCHEME` environment variable, else `kotlinstarter`. `app/build.gradle.kts` passes it to the manifest as the `${appScheme}` placeholder and to code as `BuildConfig.APP_SCHEME`. The deep link is `<scheme>://example/sdk/<demo id>` (Payment Code also takes `?amount=<IDR>`); see docs/architecture/navigation.md.
