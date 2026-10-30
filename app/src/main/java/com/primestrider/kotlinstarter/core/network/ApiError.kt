package com.primestrider.kotlinstarter.core.network

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException

/** Every HTTP failure, normalized. [message] is the server's `message` field when it sent one. */
data class ApiError(
    val message: String?,
    val status: Int? = null,
    val isNetworkError: Boolean = false,
)

sealed interface ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>

    data class Failure(
        val error: ApiError,
    ) : ApiResult<Nothing>
}

/** Runs [block] and maps every failure to [ApiResult.Failure]; coroutine cancellation is rethrown. */
suspend fun <T> safeApiCall(
    json: Json,
    block: suspend () -> T,
): ApiResult<T> =
    try {
        ApiResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        ApiResult.Failure(ApiError(e.serverMessage(json), e.code()))
    } catch (e: IOException) {
        ApiResult.Failure(ApiError(null, isNetworkError = true))
    } catch (e: SerializationException) {
        ApiResult.Failure(ApiError(null))
    }

private fun HttpException.serverMessage(json: Json): String? =
    try {
        val body = response()?.errorBody()?.string().orEmpty()
        json
            .parseToJsonElement(body)
            .jsonObject["message"]
            ?.jsonPrimitive
            ?.contentOrNull
    } catch (e: SerializationException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: IOException) {
        null
    }
