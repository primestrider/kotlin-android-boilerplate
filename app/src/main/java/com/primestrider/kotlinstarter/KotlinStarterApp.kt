package com.primestrider.kotlinstarter

import android.app.Application
import android.content.Context
import com.primestrider.kotlinstarter.core.auth.SessionExpiryAnnouncer
import com.primestrider.kotlinstarter.core.crash.CrashRecorder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KotlinStarterApp : Application() {
    @Inject lateinit var sessionExpiryAnnouncer: SessionExpiryAnnouncer

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Earliest app code: before content providers and Hilt, so crashes while they start are recorded too.
        // The injected CrashRecorder (read by AppViewModel) uses the same store.
        CrashRecorder(base).install()
    }

    override fun onCreate() {
        super.onCreate()
        sessionExpiryAnnouncer.start()
    }
}
