package com.primestrider.kotlinstarter

import android.app.Application
import com.primestrider.kotlinstarter.core.auth.SessionExpiryAnnouncer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KotlinStarterApp : Application() {
    @Inject lateinit var sessionExpiryAnnouncer: SessionExpiryAnnouncer

    override fun onCreate() {
        super.onCreate()
        sessionExpiryAnnouncer.start()
    }
}
