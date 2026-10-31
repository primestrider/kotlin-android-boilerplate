package com.primestrider.kotlinstarter.feature.example.sdk

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.feature.example.ExampleEntry
import com.primestrider.kotlinstarter.feature.example.SdkDemoRoute
import com.primestrider.kotlinstarter.feature.example.sdk.appinfo.AppInfoScreen
import com.primestrider.kotlinstarter.feature.example.sdk.clipboard.ClipboardScreen
import com.primestrider.kotlinstarter.feature.example.sdk.device.DeviceScreen
import com.primestrider.kotlinstarter.feature.example.sdk.livephoto.LivePhotoScreen
import com.primestrider.kotlinstarter.feature.example.sdk.statusbar.StatusBarScreen

/** One Android API demo in the "Device APIs" Home section. [id] is kebab-case and is the whole route. */
data class SdkDemo(
    val id: String,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val screen: @Composable (onBack: () -> Unit) -> Unit,
)

/**
 * Registry of the demos, in the order they appear on Home. To add one, append an entry here and add its
 * strings; no route, graph or catalog change is needed (see docs/examples.md).
 */
val sdkDemos: List<SdkDemo> =
    listOf(
        SdkDemo("clipboard", R.string.sdk_clipboard_title, R.string.sdk_clipboard_subtitle) { ClipboardScreen(it) },
        SdkDemo("device", R.string.sdk_device_title, R.string.sdk_device_subtitle) { DeviceScreen(it) },
        SdkDemo("app-info", R.string.sdk_app_info_title, R.string.sdk_app_info_subtitle) { AppInfoScreen(it) },
        SdkDemo("live-photo", R.string.sdk_live_photo_title, R.string.sdk_live_photo_subtitle) { LivePhotoScreen(it) },
        SdkDemo("status-bar", R.string.sdk_status_bar_title, R.string.sdk_status_bar_subtitle) { StatusBarScreen(it) },
    )

/** Home entries for the "Device APIs" section, built from [sdkDemos]. */
fun sdkEntries(): List<ExampleEntry> = sdkDemos.map { ExampleEntry(it.title, it.subtitle, SdkDemoRoute(it.id)) }
