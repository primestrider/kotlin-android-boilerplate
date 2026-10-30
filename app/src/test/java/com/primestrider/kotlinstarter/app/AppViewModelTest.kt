package com.primestrider.kotlinstarter.app

import com.primestrider.kotlinstarter.core.auth.FakeSessionCipher
import com.primestrider.kotlinstarter.core.auth.SessionRepository
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemePreferenceRepository
import com.primestrider.kotlinstarter.testing.GatedPreferencesDataStore
import com.primestrider.kotlinstarter.testing.InMemoryPreferencesDataStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun themeModeIsNullUntilFirstValueThenSystem() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val viewModel = AppViewModel(ThemePreferenceRepository(store), sessionRepository(this))
            assertNull(viewModel.themeMode.value)
            advanceUntilIdle()
            assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)
        }

    private fun sessionRepository(scope: CoroutineScope) =
        SessionRepository(InMemoryPreferencesDataStore(), FakeSessionCipher(), Json, scope)

    @Test
    fun notReadyWhileSessionStillLoading() =
        runTest {
            val store = GatedPreferencesDataStore()
            val gate = CompletableDeferred<Unit>()
            store.readGate = gate
            val session = SessionRepository(store, FakeSessionCipher(), Json, this)
            val viewModel = AppViewModel(ThemePreferenceRepository(InMemoryPreferencesDataStore()), session)
            advanceUntilIdle()
            assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)
            assertFalse(viewModel.ready.value)
            gate.complete(Unit)
            advanceUntilIdle()
            assertTrue(viewModel.ready.value)
        }

    @Test
    fun readyOnlyAfterThemeAndSessionLoaded() =
        runTest {
            val viewModel =
                AppViewModel(ThemePreferenceRepository(InMemoryPreferencesDataStore()), sessionRepository(this))
            assertFalse(viewModel.ready.value)
            advanceUntilIdle()
            assertTrue(viewModel.ready.value)
        }
}
