package com.primestrider.kotlinstarter.app.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.primestrider.kotlinstarter.feature.example.HomeScreen
import com.primestrider.kotlinstarter.feature.example.SettingsRoute
import com.primestrider.kotlinstarter.feature.example.exampleSections
import com.primestrider.kotlinstarter.feature.example.settings.SettingsScreen
import com.primestrider.kotlinstarter.feature.example.settings.SettingsViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

/** Register new destinations here: one `composable<Route>` per screen. */
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
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
                SettingsScreen(
                    state = state,
                    onThemeModeChange = viewModel::setThemeMode,
                    onLanguageChange = viewModel::setLanguage,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
