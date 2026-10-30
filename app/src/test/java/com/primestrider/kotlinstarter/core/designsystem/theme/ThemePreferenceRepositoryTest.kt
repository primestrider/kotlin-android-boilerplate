package com.primestrider.kotlinstarter.core.designsystem.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.primestrider.kotlinstarter.core.storage.StorageKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException

class ThemePreferenceRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun TestScope.newStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(tempFolder.root, "settings.preferences_pb")
        }

    @Test
    fun emitsSystemByDefault() =
        runTest {
            val repo = ThemePreferenceRepository(newStore())
            assertEquals(ThemeMode.SYSTEM, repo.themeMode.first())
        }

    @Test
    fun emitsDarkAfterSet() =
        runTest {
            val repo = ThemePreferenceRepository(newStore())
            repo.themeMode.test {
                assertEquals(ThemeMode.SYSTEM, awaitItem())
                repo.setThemeMode(ThemeMode.DARK)
                assertEquals(ThemeMode.DARK, awaitItem())
            }
        }

    @Test
    fun unknownStoredValueEmitsSystem() =
        runTest {
            val store = newStore()
            store.edit { it[StorageKeys.themeMode] = "PURPLE" }
            assertEquals(ThemeMode.SYSTEM, ThemePreferenceRepository(store).themeMode.first())
        }

    @Test
    fun ioExceptionEmitsSystem() =
        runTest {
            val failing =
                object : DataStore<Preferences> {
                    override val data: Flow<Preferences> = flow { throw IOException("boom") }

                    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
                        throw IOException("boom")
                }
            assertEquals(ThemeMode.SYSTEM, ThemePreferenceRepository(failing).themeMode.first())
        }
}
