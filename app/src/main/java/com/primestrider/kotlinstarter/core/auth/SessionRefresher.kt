package com.primestrider.kotlinstarter.core.auth

import com.primestrider.kotlinstarter.core.common.ApplicationScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface RefreshOutcome {
    /** Retry with [accessToken]. */
    data class Refreshed(
        val accessToken: String,
    ) : RefreshOutcome

    /** Signed out (already, or because the refresh was rejected): give up. */
    data object SignedOut : RefreshOutcome

    /** The refresh could not reach the server; the session is kept. */
    data object NetworkFailure : RefreshOutcome
}

/**
 * Single-flight token refresh: concurrent callers await the same attempt and share its outcome.
 * An [IOException] from the adapter is a [RefreshOutcome.NetworkFailure] and keeps the session;
 * any other failure signs out with [SignOutReason.EXPIRED]. A cancelled attempt is also a
 * [RefreshOutcome.NetworkFailure]. Catches every exception; only the caller's own cancellation propagates.
 *
 * The attempt runs in the application scope, so cancelling the call that triggered it does not
 * cancel an in-flight refresh.
 */
@Singleton
class SessionRefresher
    @Inject
    constructor(
        private val repository: SessionRepository,
        private val adapter: AuthAdapter,
        @ApplicationScope private val scope: CoroutineScope,
    ) {
        private val lock = Any()
        private var inFlight: Deferred<RefreshOutcome>? = null

        /** For tests: whether an attempt is currently registered for callers to join. */
        internal val hasAttemptInFlight: Boolean get() = synchronized(lock) { inFlight != null }

        suspend fun refresh(failedAccessToken: String?): RefreshOutcome {
            val attempt =
                synchronized(lock) {
                    val current = repository.session.value?.tokens ?: return RefreshOutcome.SignedOut
                    // Someone already refreshed since the failed request was sent.
                    if (current.accessToken != failedAccessToken) return RefreshOutcome.Refreshed(current.accessToken)
                    inFlight ?: startAttempt(current.refreshToken)
                }
            return try {
                attempt.await()
            } catch (e: CancellationException) {
                // The caller itself was cancelled: propagate. Otherwise the attempt was (the scope
                // is shutting down), which is no verdict on the session.
                currentCoroutineContext().ensureActive()
                RefreshOutcome.NetworkFailure
            }
        }

        /** Must be called holding [lock]. */
        private fun startAttempt(refreshToken: String): Deferred<RefreshOutcome> {
            val deferred =
                scope.async {
                    try {
                        attempt(refreshToken)
                    } finally {
                        // Cleared before the result is published, so no caller joins a finished attempt.
                        synchronized(lock) { inFlight = null }
                    }
                }
            inFlight = deferred
            // Also covers an attempt whose body never ran (scope already cancelled).
            deferred.invokeOnCompletion { synchronized(lock) { if (inFlight === deferred) inFlight = null } }
            return deferred
        }

        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        private suspend fun attempt(refreshToken: String): RefreshOutcome {
            val tokens =
                try {
                    adapter.refresh(refreshToken)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: IOException) {
                    return RefreshOutcome.NetworkFailure
                } catch (e: Exception) {
                    return expire()
                }
            return try {
                repository.updateTokens(tokens)
                RefreshOutcome.Refreshed(tokens.accessToken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // The old refresh token is likely spent; without storing the new one the session is unusable.
                expire()
            }
        }

        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        private suspend fun expire(): RefreshOutcome {
            try {
                if (repository.isSignedIn) repository.signOut(SignOutReason.EXPIRED)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Storage failed: the session stays as it was and the caller simply gets the 401.
            }
            return RefreshOutcome.SignedOut
        }
    }
