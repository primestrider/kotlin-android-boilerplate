package com.primestrider.kotlinstarter.core.auth

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.primestrider.kotlinstarter.testing.GatedPreferencesDataStore
import com.primestrider.kotlinstarter.testing.InMemoryPreferencesDataStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.security.GeneralSecurityException

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRepositoryTest {
    private val key = stringPreferencesKey("auth.session")
    private val json = Json { ignoreUnknownKeys = true }
    private val sample =
        Session(
            user = AuthUser(1, "emilys", "e@x.com", "Emily", "Johnson"),
            tokens = AuthTokens("access", "refresh"),
        )

    private fun TestScope.repo(
        store: InMemoryPreferencesDataStore,
        cipher: SessionCipher = FakeSessionCipher(),
    ) = SessionRepository(store, cipher, json, this)

    private suspend fun InMemoryPreferencesDataStore.raw(): Preferences = data.first()

    @Test
    fun freshStoreLoadsAsSignedOut() =
        runTest {
            val repository = repo(InMemoryPreferencesDataStore())
            assertFalse(repository.loaded.value)
            advanceUntilIdle()
            assertTrue(repository.loaded.value)
            assertNull(repository.session.value)
            assertFalse(repository.isSignedIn)
            assertNull(repository.accessToken())
        }

    @Test
    fun signInStoresEncryptedValue() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val repository = repo(store)
            advanceUntilIdle()
            repository.signIn(sample)
            assertEquals(sample, repository.session.value)
            assertEquals("access", repository.accessToken())
            val stored = store.raw()[key]
            assertNotNull(stored)
            assertFalse(stored!!.contains("refresh"))
            assertFalse(stored.contains("emilys"))
        }

    @Test
    fun newRepositoryRestoresSession() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            repo(store).signIn(sample)
            val restored = repo(store)
            advanceUntilIdle()
            assertEquals(sample, restored.session.value)
            assertTrue(restored.loaded.value)
        }

    @Test
    fun updateTokensReplacesAndPersists() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val repository = repo(store)
            advanceUntilIdle()
            repository.signIn(sample)
            val newTokens = AuthTokens("a2", "r2")
            repository.updateTokens(newTokens)
            assertEquals(newTokens, repository.session.value?.tokens)
            val restored = repo(store)
            advanceUntilIdle()
            assertEquals(newTokens, restored.session.value?.tokens)
        }

    @Test
    fun updateTokensIsNoOpWhenSignedOut() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val repository = repo(store)
            advanceUntilIdle()
            repository.updateTokens(AuthTokens("a", "r"))
            assertNull(repository.session.value)
            assertNull(store.raw()[key])
        }

    @Test
    fun signOutClearsStorageAndSetsReason() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val repository = repo(store)
            advanceUntilIdle()
            repository.signIn(sample)
            repository.signOut(SignOutReason.EXPIRED)
            assertNull(repository.session.value)
            assertNull(store.raw()[key])
            assertEquals(SignOutReason.EXPIRED, repository.signOutReason.value)
            repository.clearSignOutReason()
            assertNull(repository.signOutReason.value)
        }

    @Test
    fun signInClearsSignOutReason() =
        runTest {
            val repository = repo(InMemoryPreferencesDataStore())
            advanceUntilIdle()
            repository.signOut(SignOutReason.EXPIRED)
            repository.signIn(sample)
            assertNull(repository.signOutReason.value)
        }

    @Test
    fun garbageStoredValueIsTreatedAsSignedOutAndRemoved() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            store.edit { it[key] = "not-base64!!" }
            val repository = repo(store)
            advanceUntilIdle()
            assertTrue(repository.loaded.value)
            assertNull(repository.session.value)
            assertNull(store.raw()[key])
        }

    @Test
    fun signOutDuringUpdateTokensWriteIsNotResurrected() =
        runTest {
            val store = GatedPreferencesDataStore()
            val repository = SessionRepository(store, FakeSessionCipher(), json, this)
            advanceUntilIdle()
            repository.signIn(sample)
            val gate = CompletableDeferred<Unit>()
            store.writeGate = gate
            val update = launch { repository.updateTokens(AuthTokens("a2", "r2")) }
            runCurrent()
            val signOut = launch { repository.signOut(SignOutReason.EXPIRED) }
            runCurrent()
            gate.complete(Unit)
            advanceUntilIdle()
            assertTrue(update.isCompleted && signOut.isCompleted)
            assertNull(repository.session.value)
            assertNull(store.data.first()[key])
        }

    @Test
    fun storageReadFailureIsSignedOutButKeepsStoredData() =
        runTest {
            val store = GatedPreferencesDataStore()
            SessionRepository(store, FakeSessionCipher(), json, this).signIn(sample)
            store.readError = IOException("disk")
            val repository = SessionRepository(store, FakeSessionCipher(), json, this)
            advanceUntilIdle()
            assertTrue(repository.loaded.value)
            assertNull(repository.session.value)
            store.readError = null
            assertNotNull(store.data.first()[key])
        }

    @Test
    fun failedStorageRemovalKeepsSessionSignedIn() =
        runTest {
            val store = GatedPreferencesDataStore()
            val repository = SessionRepository(store, FakeSessionCipher(), json, this)
            advanceUntilIdle()
            repository.signIn(sample)
            store.writeError = IOException("disk")
            runCatching { repository.signOut(SignOutReason.USER) }.also { assertTrue(it.exceptionOrNull() is IOException) }
            assertEquals(sample, repository.session.value)
            assertNull(repository.signOutReason.value)
        }

    @Test
    fun cipherFailureIsTreatedAsSignedOutAndRemoved() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            repo(store).signIn(sample)
            val failing =
                object : SessionCipher {
                    override fun encrypt(plain: ByteArray) = plain

                    override fun decrypt(cipher: ByteArray): ByteArray = throw GeneralSecurityException("bad key")
                }
            val repository = repo(store, failing)
            advanceUntilIdle()
            assertTrue(repository.loaded.value)
            assertNull(repository.session.value)
            assertNull(store.raw()[key])
        }
}
