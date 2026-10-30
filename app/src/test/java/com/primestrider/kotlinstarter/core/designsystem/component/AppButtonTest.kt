package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class AppButtonTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun loading_button_ignores_clicks_and_reports_disabled() {
        var clicks = 0
        rule.setContent {
            AppTheme(false) { AppButton("Save", onClick = { clicks++ }, loading = true, modifier = Modifier.testTag("b")) }
        }
        rule.onNodeWithTag("b").assertIsNotEnabled()
        rule.onNodeWithTag("b").performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun click_fires_once() {
        var clicks = 0
        rule.setContent { AppTheme(false) { AppButton("Save", onClick = { clicks++ }) } }
        rule.onNodeWithText("Save").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun every_variant_renders_its_text() {
        rule.setContent {
            AppTheme(false) {
                Column { ButtonVariant.entries.forEach { AppButton(it.name, onClick = {}, variant = it) } }
            }
        }
        ButtonVariant.entries.forEach { rule.onNodeWithText(it.name).assertExists() }
    }
}
