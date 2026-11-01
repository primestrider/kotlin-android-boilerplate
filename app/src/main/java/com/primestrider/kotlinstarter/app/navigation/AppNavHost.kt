package com.primestrider.kotlinstarter.app.navigation

import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.primestrider.kotlinstarter.feature.example.AnimCardsRoute
import com.primestrider.kotlinstarter.feature.example.AnimListsRoute
import com.primestrider.kotlinstarter.feature.example.AnimMicroRoute
import com.primestrider.kotlinstarter.feature.example.AnimTransitionsRoute
import com.primestrider.kotlinstarter.feature.example.CatalogHome
import com.primestrider.kotlinstarter.feature.example.ComponentsHomeRoute
import com.primestrider.kotlinstarter.feature.example.ProfileRoute
import com.primestrider.kotlinstarter.feature.example.animation.cards.CardsScreen
import com.primestrider.kotlinstarter.feature.example.animation.lists.ListsScreen
import com.primestrider.kotlinstarter.feature.example.animation.micro.MicroInteractionsScreen
import com.primestrider.kotlinstarter.feature.example.animation.transitions.TransitionsScreen
import com.primestrider.kotlinstarter.feature.example.auth.accountDestinations
import com.primestrider.kotlinstarter.feature.example.catalogComponentsDestinations
import com.primestrider.kotlinstarter.feature.example.catalogHomeDestinations
import com.primestrider.kotlinstarter.feature.example.products.ui.productsDestinations
import com.primestrider.kotlinstarter.feature.example.sdk.devicesDestinations
import com.primestrider.kotlinstarter.feature.example.settings.settingsDestinations
import com.primestrider.kotlinstarter.feature.example.todos.ui.todosDestinations
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

/** One nested graph per bottom-nav tab; each keeps its own back stack. */
@Serializable
data object HomeGraph

@Serializable
data object ComponentsGraph

@Serializable
data object ProfileGraph

/**
 * Home tab: the catalog index and the pages it opens. Each feature registers its own destinations; `// region
 * example:<feature>` blocks are what `scripts/remove-examples.sh` deletes together with the feature's modules.
 */
fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    val back: () -> Unit = { navController.popBackStack() }
    navigation<HomeGraph>(startDestination = HomeRoute) {
        // region example:catalog
        composable<HomeRoute> { CatalogHome(onOpen = { route -> navController.navigate(route) }) }
        catalogHomeDestinations(navController)
        // endregion
        // region example:products
        productsDestinations(navController)
        // endregion
        // region example:todos
        todosDestinations(navController)
        // endregion
        // region example:devices
        devicesDestinations(navController)
        // endregion
        composable<AnimTransitionsRoute> { TransitionsScreen(onBack = back) }
        composable<AnimCardsRoute> { CardsScreen(onBack = back) }
        composable<AnimListsRoute> { ListsScreen(onBack = back) }
        composable<AnimMicroRoute> { MicroInteractionsScreen(onBack = back) }
    }
}

/** Components tab: the component example pages. */
fun NavGraphBuilder.componentsGraph(navController: NavHostController) {
    navigation<ComponentsGraph>(startDestination = ComponentsHomeRoute) {
        // region example:catalog
        catalogComponentsDestinations(navController)
        // endregion
    }
}

/** Profile tab: the account hub (start), sign-in, the protected Account page and Settings. */
fun NavGraphBuilder.profileGraph(navController: NavHostController) {
    navigation<ProfileGraph>(startDestination = ProfileRoute) {
        // region example:account
        accountDestinations(navController)
        // endregion
        // region example:settings
        settingsDestinations(navController)
        // endregion
    }
}
