package com.primestrider.kotlinstarter.core.auth

import com.primestrider.kotlinstarter.core.network.ApiResult
import com.primestrider.kotlinstarter.core.network.Item
import com.primestrider.kotlinstarter.core.network.NetworkModule
import com.primestrider.kotlinstarter.core.network.TestApi
import com.primestrider.kotlinstarter.core.network.safeApiCall
import com.primestrider.kotlinstarter.testing.InMemoryPreferencesDataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import javax.inject.Provider

/** End to end through the app's OkHttp client: bearer header, 401 refresh, retry and expiry. */
class AuthFlowTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val user = AuthUser(1, "emilys", "e@x.com", "Emily", "Johnson")
    private lateinit var server: MockWebServer
    private lateinit var repository: SessionRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        runCatching { server.close() }
    }

    private suspend fun TestScope.signedInApi(adapter: AuthAdapter): TestApi {
        repository = SessionRepository(InMemoryPreferencesDataStore(), FakeSessionCipher(), json, backgroundScope)
        repository.signIn(Session(user, AuthTokens("a1", "r1")))
        val refresher = SessionRefresher(repository, adapter)
        val client =
            NetworkModule
                .okHttpClient(AuthInterceptor(repository), TokenAuthenticator(Provider { refresher }))
                .apply { dispatcher.maxRequestsPerHost = CONCURRENT_CALLS }
        return Retrofit
            .Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TestApi::class.java)
    }

    private fun response(code: Int) =
        MockResponse
            .Builder()
            .code(code)
            .body(if (code == 200) """{"id":1}""" else """{"message":"Unauthorized"}""")
            .build()

    /** 200 for a request carrying `Bearer a2`, 401 for anything else. */
    private fun answerOnlyRefreshedToken() {
        server.dispatcher =
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse =
                    response(if (request.headers["Authorization"] == "Bearer a2") 200 else 401)
            }
    }

    private fun alwaysUnauthorized() {
        server.dispatcher =
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse = response(401)
            }
    }

    @Test
    fun authenticatedCallCarriesBearerAndAnonymousCallDoesNot() =
        runTest {
            val api = signedInApi(FakeAuthAdapter())
            server.enqueue(response(200))
            server.enqueue(response(200))
            api.item()
            api.open()
            assertEquals("Bearer a1", server.takeRequest().headers["Authorization"])
            assertNull(server.takeRequest().headers["Authorization"])
        }

    @Test
    fun unauthorizedIsRefreshedAndRetriedWithNewToken() =
        runTest {
            val adapter = FakeAuthAdapter()
            val api = signedInApi(adapter)
            server.enqueue(response(401))
            server.enqueue(response(200))
            assertEquals(ApiResult.Success(Item(1)), safeApiCall(json) { api.item() })
            assertEquals("Bearer a1", server.takeRequest().headers["Authorization"])
            assertEquals("Bearer a2", server.takeRequest().headers["Authorization"])
            assertEquals(AuthTokens("a2", "r2"), repository.session.value?.tokens)
            assertEquals("r1", adapter.lastRefreshToken)
            assertEquals(1, adapter.refreshCount)
        }

    @Test
    fun concurrentUnauthorizedCallsShareOneRefresh() =
        runTest {
            val adapter = FakeAuthAdapter(refreshDelayMillis = 200)
            val api = signedInApi(adapter)
            answerOnlyRefreshedToken()
            val results = List(CONCURRENT_CALLS) { async { safeApiCall(json) { api.item() } } }.awaitAll()
            assertTrue(results.toString(), results.all { it == ApiResult.Success(Item(1)) })
            assertEquals(1, adapter.refreshCount)
        }

    @Test
    fun failedRefreshSignsOutAsExpiredWithOneAttempt() =
        runTest {
            val adapter = FakeAuthAdapter(failure = IOException("refresh down"), refreshDelayMillis = 200)
            val api = signedInApi(adapter)
            alwaysUnauthorized()
            val results = List(3) { async { safeApiCall(json) { api.item() } } }.awaitAll()
            results.forEach { assertEquals(401, (it as ApiResult.Failure).error.status) }
            assertNull(repository.session.value)
            assertEquals(SignOutReason.EXPIRED, repository.signOutReason.value)
            assertEquals(1, adapter.refreshCount)
        }

    @Test
    fun unauthorizedRetryIsNotRetriedAgain() =
        runTest {
            val api = signedInApi(FakeAuthAdapter())
            alwaysUnauthorized()
            val result = safeApiCall(json) { api.item() }
            assertEquals(401, (result as ApiResult.Failure).error.status)
            assertEquals(2, server.requestCount)
        }

    private companion object {
        const val CONCURRENT_CALLS = 10
    }
}
