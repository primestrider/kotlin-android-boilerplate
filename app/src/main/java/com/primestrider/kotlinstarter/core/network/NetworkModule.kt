package com.primestrider.kotlinstarter.core.network

import com.primestrider.kotlinstarter.BuildConfig
import com.primestrider.kotlinstarter.core.auth.AuthInterceptor
import com.primestrider.kotlinstarter.core.auth.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT_SECONDS = 30L
    private const val CALL_TIMEOUT_SECONDS = 60L

    @Provides
    @Singleton
    fun json(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    /** `BuildConfig.API_URL`: the base of both Retrofits and the only host that gets the bearer. */
    @Provides
    @Singleton
    @ApiBaseUrl
    fun apiBaseUrl(): HttpUrl = BuildConfig.API_URL.toHttpUrl()

    /** The app client: bearer on every non-`@Anonymous` request to the API host, refresh and retry on 401. */
    @Provides
    @Singleton
    fun okHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient =
        baseClient()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

    /** See [RefreshClient]: a separate dispatcher, so a refresh never waits behind blocked app calls. */
    @Provides
    @Singleton
    @RefreshClient
    fun refreshOkHttpClient(): OkHttpClient = baseClient().dispatcher(Dispatcher()).build()

    @Provides
    @Singleton
    fun retrofit(
        client: OkHttpClient,
        json: Json,
        @ApiBaseUrl baseUrl: HttpUrl,
    ): Retrofit = retrofitOn(client, json, baseUrl)

    @Provides
    @Singleton
    @RefreshRetrofit
    fun refreshRetrofit(
        @RefreshClient client: OkHttpClient,
        json: Json,
        @ApiBaseUrl baseUrl: HttpUrl,
    ): Retrofit = retrofitOn(client, json, baseUrl)

    private fun baseClient(): OkHttpClient.Builder =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            // A network interceptor sees the final headers, including authenticator retries.
            .addNetworkInterceptor(
                HttpLoggingInterceptor().apply {
                    level = loggingLevel(BuildConfig.DEBUG)
                    redactHeader("Authorization")
                    redactHeader("Cookie")
                    redactHeader("Set-Cookie")
                },
            )

    private fun retrofitOn(
        client: OkHttpClient,
        json: Json,
        baseUrl: HttpUrl,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}

/** Headers only in debug: bodies carry passwords and tokens, so they are never logged. */
internal fun loggingLevel(debug: Boolean): HttpLoggingInterceptor.Level =
    if (debug) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
