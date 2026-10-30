package com.primestrider.kotlinstarter.app.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.primestrider.kotlinstarter.feature.example.ActionsRoute
import com.primestrider.kotlinstarter.feature.example.AppearanceRoute
import com.primestrider.kotlinstarter.feature.example.ColorsRoute
import com.primestrider.kotlinstarter.feature.example.HomeScreen
import com.primestrider.kotlinstarter.feature.example.LayoutRoute
import com.primestrider.kotlinstarter.feature.example.SettingsRoute
import com.primestrider.kotlinstarter.feature.example.SizingRoute
import com.primestrider.kotlinstarter.feature.example.SpacingRoute
import com.primestrider.kotlinstarter.feature.example.ThemeRoute
import com.primestrider.kotlinstarter.feature.example.TypographyRoute
import com.primestrider.kotlinstarter.feature.example.components.ActionsScreen
import com.primestrider.kotlinstarter.feature.example.exampleSections
import com.primestrider.kotlinstarter.feature.example.settings.SettingsScreen
import com.primestrider.kotlinstarter.feature.example.settings.SettingsViewModel
import com.primestrider.kotlinstarter.feature.example.styling.AppearanceScreen
import com.primestrider.kotlinstarter.feature.example.styling.ColorsScreen
import com.primestrider.kotlinstarter.feature.example.styling.LayoutScreen
import com.primestrider.kotlinstarter.feature.example.styling.SizingScreen
import com.primestrider.kotlinstarter.feature.example.styling.SpacingScreen
import com.primestrider.kotlinstarter.feature.example.styling.ThemeScreen
import com.primestrider.kotlinstarter.feature.example.styling.TypographyScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

/** Register new destinations here: one `composable<Route>` per screen. */
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val back: () -> Unit = { navController.popBackStack() }
    Surface {
        NavHost(navController = navController, startDestination = HomeRoute) {
            composable<HomeRoute> {
                HomeScreen(
                    sections = exampleSections,
                    onOpen = { route -> navController.navigate(route) },
                )
            }
            composable<SettingsRoute> {
                val viewModel: SettingsViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                LifecycleResumeEffect(Unit) {
                    viewModel.refreshLanguage()
                    onPauseOrDispose {}
                }
                SettingsScreen(
                    state = state,
                    onThemeModeChange = viewModel::setThemeMode,
                    onLanguageChange = viewModel::setLanguage,
                    onBack = back,
                )
            }
            composable<ActionsRoute> { ActionsScreen(onBack = back) }
            composable<ColorsRoute> { ColorsScreen(onBack = back) }
            composable<TypographyRoute> { TypographyScreen(onBack = back) }
            composable<SpacingRoute> { SpacingScreen(onBack = back) }
            composable<SizingRoute> { SizingScreen(onBack = back) }
            composable<LayoutRoute> { LayoutScreen(onBack = back) }
            composable<AppearanceRoute> { AppearanceScreen(onBack = back) }
            composable<ThemeRoute> {
                // Reuses the Settings state holder: the theme preference logic lives there only.
                val viewModel: SettingsViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                ThemeScreen(
                    themeMode = state.themeMode,
                    onThemeModeChange = viewModel::setThemeMode,
                    onBack = back,
                )
            }
        }
    }
}
