package com.primestrider.kotlinstarter.core.network

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class SafeApiCallTest {
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var server: MockWebServer
    private lateinit var api: TestApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api =
            Retrofit
                .Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(TestApi::class.java)
    }

    @After
    fun tearDown() {
        runCatching { server.close() }
    }

    private fun respond(
        code: Int,
        body: String,
    ) = server.enqueue(
        MockResponse
            .Builder()
            .code(code)
            .body(body)
            .build(),
    )

    @Test
    fun successReturnsData() =
        runTest {
            respond(200, """{"id":1}""")
            assertEquals(ApiResult.Success(Item(1)), safeApiCall(json) { api.item() })
        }

    @Test
    fun httpErrorWithMessageBody() =
        runTest {
            respond(404, """{"message":"Not found"}""")
            assertEquals(
                ApiResult.Failure(ApiError("Not found", 404)),
                safeApiCall(json) { api.item() },
            )
        }

    @Test
    fun httpErrorWithNonJsonBody() =
        runTest {
            respond(500, "oops")
            assertEquals(
                ApiResult.Failure(ApiError(null, 500)),
                safeApiCall(json) { api.item() },
            )
        }

    @Test
    fun unreachableServerIsNetworkError() =
        runTest {
            server.close()
            val result = safeApiCall(json) { api.item() }
            assertTrue(result is ApiResult.Failure && result.error.isNetworkError)
        }

    @Test
    fun malformedJsonIsSerializationFailure() =
        runTest {
            respond(200, "{not json")
            assertEquals(
                ApiResult.Failure(ApiError(null, null)),
                safeApiCall(json) { api.item() },
            )
        }
}
