package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
class ChoiceControlsTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun segmented_choice_reports_the_clicked_option() {
        var picked: String? = null
        rule.setContent {
            AppTheme(false) {
                SegmentedChoice(options = listOf("a", "b", "c"), selected = "a", onSelect = { picked = it }, label = { "Option $it" })
            }
        }
        rule.onNodeWithText("Option b").performClick()
        assertEquals("b", picked)
    }

    @Test
    fun switch_row_toggles_from_a_click_on_the_label_and_has_switch_role() {
        var checked = false
        rule.setContent { AppTheme(false) { SwitchRow(label = "Hidden", checked = checked, onCheckedChange = { checked = it }) } }
        rule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch)).assertIsDisplayed()
        rule.onNodeWithText("Hidden").performClick()
        assertEquals(true, checked)
    }

    @Test
    fun switch_row_has_one_click_target() {
        rule.setContent { AppTheme(false) { SwitchRow(label = "Hidden", checked = false, onCheckedChange = {}) } }
        assertEquals(1, rule.onAllNodes(hasClickAction()).fetchSemanticsNodes().size)
    }

    @Test
    fun labeled_slider_shows_label_and_value_text() {
        rule.setContent { AppTheme(false) { LabeledSlider("Brightness", 0.5f, {}, 0f..1f, valueText = "50%") } }
        rule.onNodeWithText("Brightness").assertIsDisplayed()
        rule.onNodeWithText("50%").assertIsDisplayed()
    }

    @Test
    fun info_card_renders_a_dash_for_null_and_blank() {
        rule.setContent { AppTheme(false) { InfoCard(rows = listOf("Name" to "Starter", "Empty" to null, "Blank" to "  ")) } }
        rule.onNodeWithText("Starter").assertIsDisplayed()
        rule.onAllNodesWithText("—").assertCountEquals(2)
    }
}
