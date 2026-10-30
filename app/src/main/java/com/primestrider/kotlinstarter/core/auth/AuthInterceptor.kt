package com.primestrider.kotlinstarter.core.auth

import com.primestrider.kotlinstarter.core.network.isAnonymous
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Adds `Authorization: Bearer <accessToken>` to every request except `@Anonymous` ones. */
class AuthInterceptor
    @Inject
    constructor(
        private val repository: SessionRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val token = repository.accessToken()
            if (token == null || request.isAnonymous()) return chain.proceed(request)
            return chain.proceed(request.newBuilder().header(AUTHORIZATION, bearer(token)).build())
        }
    }

internal const val AUTHORIZATION = "Authorization"
private const val BEARER_PREFIX = "Bearer "

internal fun bearer(token: String): String = BEARER_PREFIX + token

/** The token of a `Bearer` authorization header, or null when there is none. */
internal fun bearerToken(header: String?): String? = header?.takeIf { it.startsWith(BEARER_PREFIX) }?.removePrefix(BEARER_PREFIX)
