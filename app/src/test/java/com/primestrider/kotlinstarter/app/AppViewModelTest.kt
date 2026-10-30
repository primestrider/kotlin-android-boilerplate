package com.primestrider.kotlinstarter.app

import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemePreferenceRepository
import com.primestrider.kotlinstarter.testing.InMemoryPreferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
            val viewModel = AppViewModel(ThemePreferenceRepository(store))
            assertNull(viewModel.themeMode.value)
            advanceUntilIdle()
            assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)
        }
}
