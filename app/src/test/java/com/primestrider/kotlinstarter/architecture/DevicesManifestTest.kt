package com.primestrider.kotlinstarter.architecture

import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/** The Device APIs feature declares its permissions in its own manifest; the merged app manifest must keep them. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class DevicesManifestTest {
    @Test
    fun merged_manifest_keeps_device_permissions() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        @Suppress("DEPRECATION")
        val requested =
            context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                .requestedPermissions
                .orEmpty()
                .toSet()
        val expected =
            setOf(
                "android.permission.INTERNET",
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.READ_CALENDAR",
                "android.permission.WRITE_CALENDAR",
                "android.permission.READ_CONTACTS",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO",
                "android.permission.USE_BIOMETRIC",
                "android.permission.DETECT_SCREEN_CAPTURE",
            )
        assertTrue("missing ${expected - requested}", requested.containsAll(expected))
    }
}
