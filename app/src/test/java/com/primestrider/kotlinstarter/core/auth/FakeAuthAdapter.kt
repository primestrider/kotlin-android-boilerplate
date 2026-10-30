package com.primestrider.kotlinstarter.core.auth

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

/** Counts refreshes; returns [next] after [refreshDelayMillis], or throws [failure] when set. */
class FakeAuthAdapter(
    private val next: AuthTokens = AuthTokens("a2", "r2"),
    private val failure: Throwable? = null,
    private val refreshDelayMillis: Long = 0,
) : AuthAdapter {
    private val count = AtomicInteger()
    val refreshCount: Int get() = count.get()

    @Volatile var lastRefreshToken: String? = null
        private set

    override suspend fun signIn(credentials: SignInCredentials): AuthResult = error("not used")

    override suspend fun refresh(refreshToken: String): AuthTokens {
        count.incrementAndGet()
        lastRefreshToken = refreshToken
        if (refreshDelayMillis > 0) delay(refreshDelayMillis)
        failure?.let { throw it }
        return next
    }
}
