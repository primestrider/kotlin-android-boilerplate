package com.primestrider.kotlinstarter.feature.example.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemePreferenceRepository
import com.primestrider.kotlinstarter.core.i18n.AppLanguage
import com.primestrider.kotlinstarter.core.i18n.LanguageController
import com.primestrider.kotlinstarter.testing.InMemoryPreferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class SettingsViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = SettingsViewModel(ThemePreferenceRepository(InMemoryPreferencesDataStore()), LanguageController())

    @Test
    fun setThemeModeUpdatesState() =
        runTest {
            val vm = viewModel()
            vm.setThemeMode(ThemeMode.LIGHT)
            assertEquals(ThemeMode.LIGHT, vm.state.value.themeMode)
        }

    @Test
    fun setLanguageUpdatesState() =
        runTest {
            val vm = viewModel()
            vm.setLanguage(AppLanguage.ID)
            assertEquals(AppLanguage.ID, vm.state.value.language)
        }
}
