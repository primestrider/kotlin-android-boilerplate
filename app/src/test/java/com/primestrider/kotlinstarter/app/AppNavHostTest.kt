package com.primestrider.kotlinstarter.app

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.primestrider.kotlinstarter.app.navigation.AppNavHost
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import com.primestrider.kotlinstarter.feature.example.exampleSections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class, sdk = [36])
class AppNavHostTest {
    private val hiltRule = HiltAndroidRule(this)
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(hiltRule).around(composeRule)

    @Test
    fun everyCatalogEntryIsRegisteredAndShowsItsPage() {
        lateinit var navController: NavHostController
        // Replaces MainActivity's own content so the test controls the NavHostController.
        composeRule.activity.setContent {
            navController = rememberNavController()
            AppTheme(darkTheme = false) { AppNavHost(navController) }
        }
        composeRule.waitForIdle()
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val entries = exampleSections.flatMap { it.entries }
        assertTrue(entries.isNotEmpty())
        entries.forEach { entry ->
            composeRule.runOnUiThread { navController.navigate(entry.route) }
            composeRule.waitForIdle()
            composeRule.onNodeWithText(context.getString(entry.title)).assertIsDisplayed()
            composeRule.runOnUiThread { navController.popBackStack() }
            composeRule.waitForIdle()
        }
    }
}
