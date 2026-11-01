package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.primestrider.kotlinstarter.core.designsystem.theme.PreviewTheme
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemePreviews

const val APP_NAVIGATION_RAIL_TAG = "app_navigation_rail"

/**
 * Top-level navigation that follows the window size: a bottom [AppNavigationBar] on compact width (phones in portrait),
 * a navigation rail at the start from medium width (tablets, foldables, phones in landscape). With one item there is
 * nothing to switch to, so only [content] shows. The type comes from Material's adaptive defaults
 * (`NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo`); the bar keeps this app's look and badges.
 */
@Composable
fun AppNavigationSuite(
    items: List<NavBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (items.size <= 1) {
        Box(modifier.fillMaxSize()) { content() }
        return
    }
    val type = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    if (type == NavigationSuiteType.NavigationBar) {
        Column(modifier.fillMaxSize()) {
            // The bar pads itself for the system navigation bar, so the screens above must not pad for it again.
            Box(Modifier.weight(1f).consumeWindowInsets(WindowInsets.navigationBars)) { content() }
            AppNavigationBar(items = items, selectedIndex = selectedIndex, onSelect = onSelect)
        }
    } else {
        Row(modifier.fillMaxSize()) {
            NavigationRail(Modifier.fillMaxHeight().testTag(APP_NAVIGATION_RAIL_TAG)) {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                        icon = { NavBarIcon(item) },
                        label = { Text(item.label) },
                    )
                }
            }
            // The rail takes the start-side insets (cutout, landscape navigation bar); the screens keep the rest.
            Box(Modifier.weight(1f).consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))) { content() }
        }
    }
}

@ThemePreviews
@Composable
private fun AppNavigationSuitePreview() {
    PreviewTheme {
        AppNavigationSuite(
            items = listOf(NavBarItem("Home", Icons.Filled.Home), NavBarItem("Profile", Icons.Filled.Person)),
            selectedIndex = 0,
            onSelect = {},
        ) { Text("Content") }
    }
}
