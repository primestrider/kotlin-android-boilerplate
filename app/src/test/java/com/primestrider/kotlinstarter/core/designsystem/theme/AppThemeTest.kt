package com.primestrider.kotlinstarter.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class AppThemeTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun capture(dark: Boolean): Pair<Color, Color> {
        var result: Pair<Color, Color>? = null
        composeRule.setContent {
            AppTheme(darkTheme = dark) {
                result = MaterialTheme.colorScheme.background to AppThemeTokens.colors.success
            }
        }
        composeRule.waitForIdle()
        return checkNotNull(result)
    }

    @Test
    fun dark_theme_provides_dark_colors() {
        val (background, success) = capture(dark = true)
        assertEquals(DarkColors.background, background)
        assertEquals(DarkColors.success, success)
    }

    @Test
    fun light_theme_provides_light_colors() {
        val (background, success) = capture(dark = false)
        assertEquals(LightColors.background, background)
        assertEquals(LightColors.success, success)
    }
}
