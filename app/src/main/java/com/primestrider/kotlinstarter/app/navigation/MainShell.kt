package com.primestrider.kotlinstarter.app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.AppNavigationBar
import com.primestrider.kotlinstarter.core.designsystem.component.LocalMessenger
import com.primestrider.kotlinstarter.core.designsystem.component.MessengerHost
import com.primestrider.kotlinstarter.core.designsystem.component.NavBarItem
import com.primestrider.kotlinstarter.feature.example.ComponentsHomeRoute
import com.primestrider.kotlinstarter.feature.example.SettingsRoute
import kotlin.reflect.KClass

/** Top-level destinations shown in the bottom navigation bar, in display order. */
enum class TopLevelTab(
    @StringRes val label: Int,
    val icon: ImageVector,
    val graph: Any,
    val startRoute: Any,
    val graphClass: KClass<*>,
) {
    HOME(R.string.nav_home, Icons.Filled.Home, HomeGraph, HomeRoute, HomeGraph::class),
    COMPONENTS(R.string.nav_components, Icons.AutoMirrored.Filled.List, ComponentsGraph, ComponentsHomeRoute, ComponentsGraph::class),
    PROFILE(R.string.nav_profile, Icons.Filled.Person, ProfileGraph, SettingsRoute, ProfileGraph::class),
}

/**
 * App shell: bottom navigation over one nested graph per tab.
 *
 * - Switching tabs saves and restores each tab's back stack.
 * - Re-selecting the current tab pops it back to its root.
 * - Back at the root of a non-Home tab returns to Home: tab switches keep Home's start destination
 *   underneath (popUpTo the start destination), so Navigation's default back handles it.
 */
@Composable
fun MainShell(navController: NavHostController = rememberNavController()) {
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val current =
        TopLevelTab.entries.firstOrNull { tab -> destination?.hierarchy?.any { it.hasRoute(tab.graphClass) } == true }
            ?: TopLevelTab.HOME

    fun open(tab: TopLevelTab) {
        if (tab == current) {
            navController.popBackStack(tab.startRoute, inclusive = false)
            return
        }
        navController.navigate(tab.graph) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { MessengerHost(LocalMessenger.current, snackbarHostState) },
        bottomBar = {
            AppNavigationBar(
                items = TopLevelTab.entries.map { NavBarItem(stringResource(it.label), it.icon) },
                selectedIndex = current.ordinal,
                onSelect = { open(TopLevelTab.entries[it]) },
            )
        },
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = HomeGraph,
            modifier = Modifier.padding(bottom = inner.calculateBottomPadding()).consumeWindowInsets(inner),
        ) {
            homeGraph(navController)
            componentsGraph(navController)
            profileGraph()
        }
    }
}
