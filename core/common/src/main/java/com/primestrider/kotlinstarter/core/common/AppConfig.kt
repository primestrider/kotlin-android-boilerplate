package com.primestrider.kotlinstarter.core.common

/** Build values every module may read. The scheme matches the app's deep-link manifest placeholder. */
object AppConfig {
    /** Deep link scheme, `<scheme>://example/...`; set from `app.scheme` / APP_SCHEME at build time. */
    val appScheme: String = BuildConfig.APP_SCHEME
}
