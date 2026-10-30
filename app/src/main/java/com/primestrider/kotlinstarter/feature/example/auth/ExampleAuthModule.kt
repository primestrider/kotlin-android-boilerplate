package com.primestrider.kotlinstarter.feature.example.auth

import com.primestrider.kotlinstarter.core.auth.AuthAdapter
import com.primestrider.kotlinstarter.core.auth.AuthResult
import com.primestrider.kotlinstarter.core.auth.AuthTokens
import com.primestrider.kotlinstarter.core.auth.SignInCredentials
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
abstract class ExampleAuthModule {
    @Binds
    abstract fun bindAuthAdapter(impl: DummyJsonAuthAdapter): AuthAdapter
}

/** Placeholder until the DummyJSON adapter lands. */
class DummyJsonAuthAdapter
    @Inject
    constructor() : AuthAdapter {
        override suspend fun signIn(credentials: SignInCredentials): AuthResult = throw NotImplementedError()

        override suspend fun refresh(refreshToken: String): AuthTokens = throw NotImplementedError()
    }
