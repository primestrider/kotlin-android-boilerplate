# Release

## Flavours

One dimension, `environment`:

| Flavour | Application id | API base URL (default) |
| --- | --- | --- |
| `dev` | `com.primestrider.kotlinstarter.dev` (installs next to prod), version name `-dev` | `https://dummyjson.com/` |
| `prod` | `com.primestrider.kotlinstarter` | `https://dummyjson.com/` |

There is no real backend yet, so both point at DummyJSON; a real app changes the `prod` default in `apiUrl(...)` in
`app/build.gradle.kts`. `local.properties` `api.url` or the env var `API_URL` overrides either. `:app` provides the URL to
the rest of the app as `@ApiBaseUrl` (`app/src/main/java/com/primestrider/kotlinstarter/app/di/EnvironmentModule.kt`),
and only that host receives the bearer token.

## Signing

The release signing config reads, from `local.properties` (never committed) or environment variables:

| `local.properties` | Environment |
| --- | --- |
| `android.keystore.path` | `ANDROID_KEYSTORE_PATH` |
| `android.keystore.password` | `ANDROID_KEYSTORE_PASSWORD` |
| `android.key.alias` | `ANDROID_KEY_ALIAS` |
| `android.key.password` | `ANDROID_KEY_PASSWORD` |

Without a keystore path the release build is signed with the debug key, so a fresh clone and CI still build and
install it. Never ship that build. Check a signature with `apksigner verify --print-certs <apk>`.

## R8

Release builds minify and shrink resources. Keep rules: `app/proguard-rules.pro` (navigation routes keep their names)
and each module's `consumer-rules.pro` for the libraries it uses (e.g. `feature/devices/impl/consumer-rules.pro`).
`./gradlew assembleProdRelease verifyReleaseKeeps` builds the minified release and fails if R8 renamed a
`@Serializable` route of a feature `:api` module. After changing dependencies, install the prod release on a device and
open Home, a list, a detail page and sign-in.

## Baseline profile

`:baselineprofile` holds a startup journey (`StartupBaselineProfile`) and a startup benchmark (`StartupBenchmark`).
Both need a connected device or emulator on API 28+:

```
./gradlew :app:generateProdReleaseBaselineProfile
```

writes the profile under `app/src/prodRelease/generated/baselineProfiles/`; commit it, and release builds package it
(`profileinstaller` installs it where Play does not). Measure the gain with
`./gradlew :baselineprofile:connectedProdBenchmarkReleaseAndroidTest`. Until a profile is committed, release builds
simply ship without one.

## CI

`.github/workflows/ci.yml` (GitHub Actions, Linux):

- `check`: `./gradlew test ktlintCheck lint assembleDevDebug assembleProdRelease verifyReleaseKeeps`, plus the
  screenshot verification once reference images exist; reports are uploaded when it fails.
- `examples-removed`: runs `scripts/remove-examples.sh` on a copy and builds and tests it.
- `record-screenshots`: manual (`workflow_dispatch` with `record_screenshots`), uploads new Roborazzi references.
