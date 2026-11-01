package com.primestrider.kotlinstarter.app.di

import com.primestrider.kotlinstarter.BuildConfig
import com.primestrider.kotlinstarter.core.network.ApiBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Singleton

/** The app owns the environment: `BuildConfig.API_URL` is the base of both Retrofits and the only host that gets the bearer. */
@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule {
    @Provides
    @Singleton
    @ApiBaseUrl
    fun apiBaseUrl(): HttpUrl = BuildConfig.API_URL.toHttpUrl()
}
