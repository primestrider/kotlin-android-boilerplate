package com.primestrider.kotlinstarter.core.auth

import com.primestrider.kotlinstarter.core.network.isAnonymous
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

/**
 * On a 401 of an authenticated request, refreshes once (shared with concurrent 401s) and retries
 * once with the new token. Gives up when the request is anonymous, carried no bearer, or was already
 * the retry. [Provider] breaks the client -> refresher -> adapter -> client dependency cycle.
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
            // OkHttp calls this on its own background thread, so blocking here is expected.
            val newToken = runBlocking { refresher.get().refresh(failedToken) } ?: return null
            return request.newBuilder().header(AUTHORIZATION, bearer(newToken)).build()
        }
    }
