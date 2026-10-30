package com.primestrider.kotlinstarter.core.auth

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single-flight token refresh: concurrent 401s queue on one mutex, the first refreshes and the rest
 * reuse its result. A failed refresh signs out with [SignOutReason.EXPIRED]. Never throws, except
 * for coroutine cancellation.
 */
@Singleton
class SessionRefresher
    @Inject
    constructor(
        private val repository: SessionRepository,
        private val adapter: AuthAdapter,
    ) {
        private val lock = Mutex()

        /** Returns the access token to retry with, or null to give up. */
        suspend fun refresh(failedAccessToken: String?): String? =
            lock.withLock {
                val current = repository.session.value?.tokens ?: return@withLock null
                // Someone refreshed while this caller waited for the lock.
                if (current.accessToken != failedAccessToken) return@withLock current.accessToken
                try {
                    val tokens = adapter.refresh(current.refreshToken)
                    repository.updateTokens(tokens)
                    tokens.accessToken
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    expire()
                    null
                }
            }

        private suspend fun expire() {
            try {
                if (repository.isSignedIn) repository.signOut(SignOutReason.EXPIRED)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Storage failed: the session stays as it was and the caller simply gets the 401.
            }
        }
    }
