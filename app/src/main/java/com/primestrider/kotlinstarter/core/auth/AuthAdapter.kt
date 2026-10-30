package com.primestrider.kotlinstarter.core.auth

data class AuthResult(
    val user: AuthUser,
    val tokens: AuthTokens,
)

/**
 * The only backend-specific piece of auth: how to sign in and how to trade a refresh token for new
 * tokens. Bind one implementation in a feature module. Its refresh request must be `@Anonymous`.
 */
interface AuthAdapter {
    suspend fun signIn(credentials: SignInCredentials): AuthResult

    suspend fun refresh(refreshToken: String): AuthTokens
}
