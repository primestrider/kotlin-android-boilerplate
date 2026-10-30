package com.primestrider.kotlinstarter.core.auth

import com.primestrider.kotlinstarter.testing.GatedPreferencesDataStore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class SessionRefresherTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val user = AuthUser(1, "emilys", "e@x.com", "Emily", "Johnson")

    private fun TestScope.repo(store: GatedPreferencesDataStore = GatedPreferencesDataStore()) =
        SessionRepository(store, FakeSessionCipher(), json, backgroundScope)

    @Test
    fun staleFailedTokenReturnsCurrentTokenWithoutCallingAdapter() =
        runTest {
            val repository = repo()
            repository.signIn(Session(user, AuthTokens("a2", "r2")))
            val adapter = FakeAuthAdapter()
            assertEquals("a2", SessionRefresher(repository, adapter).refresh("a1"))
            assertEquals(0, adapter.refreshCount)
        }

    @Test
    fun signedOutReturnsNullWithoutCallingAdapter() =
        runTest {
            val adapter = FakeAuthAdapter()
            assertNull(SessionRefresher(repo(), adapter).refresh("a1"))
            assertEquals(0, adapter.refreshCount)
        }

    @Test
    fun refreshStoresAndReturnsNewToken() =
        runTest {
            val repository = repo()
            repository.signIn(Session(user, AuthTokens("a1", "r1")))
            val adapter = FakeAuthAdapter()
            assertEquals("a2", SessionRefresher(repository, adapter).refresh("a1"))
            assertEquals(AuthTokens("a2", "r2"), repository.session.value?.tokens)
            assertEquals("r1", adapter.lastRefreshToken)
        }

    @Test
    fun failedRefreshSignsOutAsExpired() =
        runTest {
            val repository = repo()
            repository.signIn(Session(user, AuthTokens("a1", "r1")))
            val adapter = FakeAuthAdapter(failure = IOException("down"))
            assertNull(SessionRefresher(repository, adapter).refresh("a1"))
            assertNull(repository.session.value)
            assertEquals(SignOutReason.EXPIRED, repository.signOutReason.value)
        }

    @Test
    fun storageFailureDuringExpirySignOutDoesNotThrow() =
        runTest {
            val store = GatedPreferencesDataStore()
            val repository = repo(store)
            repository.signIn(Session(user, AuthTokens("a1", "r1")))
            store.writeError = IOException("disk")
            val adapter = FakeAuthAdapter(failure = IOException("down"))
            assertNull(SessionRefresher(repository, adapter).refresh("a1"))
        }
}
