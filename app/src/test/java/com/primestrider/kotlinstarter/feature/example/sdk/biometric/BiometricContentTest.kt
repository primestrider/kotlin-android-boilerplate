package com.primestrider.kotlinstarter.feature.example.sdk.biometric

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.BannerVariant
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class BiometricContentTest {
    @get:Rule
    val rule = createComposeRule()

    private val enrolled = BiometricSupport(hardware = true, enrolled = true, strength = BiometricStrength.STRONG, secureStorage = true)
    private var dismissed = 0

    private fun show(
        support: BiometricSupport = enrolled,
        fallbackSupported: Boolean = true,
        hasPin: Boolean = false,
        status: BiometricStatus? = null,
    ) {
        rule.setContent {
            AppTheme(false) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    BiometricContent(
                        support = support,
                        fallbackSupported = fallbackSupported,
                        allowFallback = fallbackSupported,
                        pin = "",
                        hasPin = hasPin,
                        status = status,
                        onAllowFallback = {},
                        onVerify = {},
                        onPin = {},
                        onSave = {},
                        onReveal = {},
                        onDelete = {},
                        onDismissStatus = { dismissed++ },
                    )
                }
            }
        }
    }

    @Test
    fun support_rows_and_verify_for_an_enrolled_device() {
        show()
        rule.onNodeWithText("Strong").assertIsDisplayed()
        rule.onNodeWithText("Available").assertIsDisplayed()
        rule.onNodeWithText("Verify it's me").assertIsEnabled()
        rule.onNodeWithText("Passcode fallback with a protected key needs Android 11.").assertDoesNotExist()
    }

    @Test
    fun verify_is_disabled_without_enrolment() {
        show(support = enrolled.copy(enrolled = false, strength = null))
        rule.onNodeWithText("Verify it's me").assertIsNotEnabled()
    }

    @Test
    fun below_android_11_the_fallback_switch_is_off_and_explained() {
        show(fallbackSupported = false)
        rule.onNodeWithText("Allow device passcode fallback").assertIsNotEnabled()
        rule.onNodeWithText("Passcode fallback with a protected key needs Android 11.").assertIsDisplayed()
    }

    @Test
    fun reveal_and_delete_need_a_stored_pin() {
        show()
        rule.onNodeWithText("Reveal").performScrollTo().assertIsNotEnabled()
        rule.onNodeWithText("Delete").assertIsNotEnabled()
    }

    @Test
    fun the_status_banner_shows_and_closes() {
        show(hasPin = true, status = BiometricStatus(R.string.sdk_biometric_status_stored, BannerVariant.INFO, "123456"))
        rule.onNodeWithText("Reveal").assertIsEnabled()
        rule.onNodeWithText("Stored PIN: 123456").performScrollTo().assertIsDisplayed()
        rule.onNodeWithContentDescription("Stored PIN: 123456", substring = true).performClick()
        assertEquals(1, dismissed)
    }
}
