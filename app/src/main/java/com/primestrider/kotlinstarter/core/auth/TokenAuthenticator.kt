package com.primestrider.kotlinstarter.core.auth

import com.primestrider.kotlinstarter.core.network.isAnonymous
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider

/**
 * On a 401 of an authenticated request, refreshes once (shared with concurrent 401s) and retries
 * once with the new token. Gives up when the request is anonymous, carried no bearer, or was already
 * the retry. A refresh that could not reach the server fails the call with an [IOException], so the
 * caller sees a network error rather than the 401.
 * [Provider] breaks the client -> refresher -> adapter -> client dependency cycle.
 */
class TokenAuthenticator
    @Inject
    constructor(
        private val refresher: Provider<SessionRefresher>,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            val request = response.request
            if (request.isAnonymous() || response.priorResponse != null) return null
            val failedToken = bearerToken(request.header(AUTHORIZATION)) ?: return null
            return when (val outcome = refresh(failedToken)) {
                is RefreshOutcome.Refreshed -> request.newBuilder().header(AUTHORIZATION, bearer(outcome.accessToken)).build()
                RefreshOutcome.SignedOut -> null
                RefreshOutcome.NetworkFailure -> throw IOException("Token refresh failed: network")
            }
        }

        // OkHttp calls this on its own background thread, so blocking here is expected.
        // Only IOExceptions may escape: anything else would crash the OkHttp thread.
        private fun refresh(failedToken: String): RefreshOutcome =
            try {
                runBlocking { refresher.get().refresh(failedToken) }
            } catch (e: CancellationException) {
                throw IOException("Token refresh canceled", e)
            }
    }
