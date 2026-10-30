package com.primestrider.kotlinstarter

import android.app.Application
import com.primestrider.kotlinstarter.core.auth.SessionExpiryAnnouncer
import com.primestrider.kotlinstarter.core.crash.CrashRecorder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KotlinStarterApp : Application() {
    @Inject lateinit var crashRecorder: CrashRecorder

    @Inject lateinit var sessionExpiryAnnouncer: SessionExpiryAnnouncer

    override fun onCreate() {
        super.onCreate() // Hilt injects here
        crashRecorder.install() // first, so startup crashes are recorded too
        sessionExpiryAnnouncer.start()
    }
}
