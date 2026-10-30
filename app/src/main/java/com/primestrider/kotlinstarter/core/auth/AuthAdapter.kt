package com.primestrider.kotlinstarter.core.auth

data class AuthResult(
    val user: AuthUser,
    val tokens: AuthTokens,
)

/**
 * The only backend-specific piece of auth: how to sign in and how to trade a refresh token for new
 * tokens. Bind one implementation in a feature module.
 *
 * [refresh] must use the `@RefreshRetrofit` / `@RefreshClient` (`core/network`), never the app client:
 * the app client's dispatcher threads are blocked while a refresh runs. Throw an `IOException` when
 * the server cannot be reached (the session is kept); any other exception signs the user out.
 */
interface AuthAdapter {
    suspend fun signIn(credentials: SignInCredentials): AuthResult

    suspend fun refresh(refreshToken: String): AuthTokens
}
