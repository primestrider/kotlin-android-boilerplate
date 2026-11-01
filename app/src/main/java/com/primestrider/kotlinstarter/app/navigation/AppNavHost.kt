package com.primestrider.kotlinstarter.app.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.primestrider.kotlinstarter.BuildConfig
import com.primestrider.kotlinstarter.feature.example.AccountRoute
import com.primestrider.kotlinstarter.feature.example.AnimCardsRoute
import com.primestrider.kotlinstarter.feature.example.AnimListsRoute
import com.primestrider.kotlinstarter.feature.example.AnimMicroRoute
import com.primestrider.kotlinstarter.feature.example.AnimTransitionsRoute
import com.primestrider.kotlinstarter.feature.example.CatalogHome
import com.primestrider.kotlinstarter.feature.example.ComponentsHomeRoute
import com.primestrider.kotlinstarter.feature.example.ProfileRoute
import com.primestrider.kotlinstarter.feature.example.SdkDemoRoute
import com.primestrider.kotlinstarter.feature.example.SettingsRoute
import com.primestrider.kotlinstarter.feature.example.SignInRoute
import com.primestrider.kotlinstarter.feature.example.TodosRoute
import com.primestrider.kotlinstarter.feature.example.animation.cards.CardsScreen
import com.primestrider.kotlinstarter.feature.example.animation.lists.ListsScreen
import com.primestrider.kotlinstarter.feature.example.animation.micro.MicroInteractionsScreen
import com.primestrider.kotlinstarter.feature.example.animation.transitions.TransitionsScreen
import com.primestrider.kotlinstarter.feature.example.auth.AccountScreen
import com.primestrider.kotlinstarter.feature.example.auth.AccountViewModel
import com.primestrider.kotlinstarter.feature.example.auth.ProfileScreen
import com.primestrider.kotlinstarter.feature.example.auth.SignInScreen
import com.primestrider.kotlinstarter.feature.example.auth.SignInViewModel
import com.primestrider.kotlinstarter.feature.example.catalogComponentsDestinations
import com.primestrider.kotlinstarter.feature.example.catalogHomeDestinations
import com.primestrider.kotlinstarter.feature.example.products.ui.productsDestinations
import com.primestrider.kotlinstarter.feature.example.sdk.SdkDemoScreen
import com.primestrider.kotlinstarter.feature.example.settings.SettingsScreen
import com.primestrider.kotlinstarter.feature.example.settings.SettingsViewModel
import com.primestrider.kotlinstarter.feature.example.todos.TodosScreen
import com.primestrider.kotlinstarter.feature.example.todos.TodosViewModel
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
        composable<TodosRoute> {
            val viewModel: TodosViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            TodosScreen(
                state = state,
                onInputChange = viewModel::onInputChange,
                onAdd = viewModel::add,
                onToggle = viewModel::toggle,
                onRemove = viewModel::remove,
                onClearCompleted = viewModel::clearCompleted,
                onFilterChange = viewModel::setFilter,
                onBack = back,
            )
        }
        composable<SdkDemoRoute>(
            deepLinks = listOf(navDeepLink<SdkDemoRoute>(basePath = "${BuildConfig.APP_SCHEME}://example/sdk")),
        ) { entry -> SdkDemoScreen(id = entry.toRoute<SdkDemoRoute>().id, onBack = back) }
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

/**
 * Profile tab: the Profile hub (start), Settings, sign-in, and the protected Account page.
 * Account guard: once the stored session has been read, a missing session while Account is showing (expiry, or none
 * stored when Account is restored) replaces Account with sign-in, so back from sign-in never returns to Account.
 * A user sign-out returns to the Profile hub instead.
 */
fun NavGraphBuilder.profileGraph(navController: NavHostController) {
    val back: () -> Unit = { navController.popBackStack() }
    navigation<ProfileGraph>(startDestination = ProfileRoute) {
        composable<ProfileRoute> {
            // The account state holder doubles as the session source for the hub.
            val viewModel: AccountViewModel = hiltViewModel()
            val session by viewModel.session.collectAsStateWithLifecycle()
            ProfileScreen(
                session = session,
                onSignIn = { navController.navigate(SignInRoute) },
                onOpenAccount = { navController.navigate(AccountRoute) },
                onOpenSettings = { navController.navigate(SettingsRoute) },
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
        composable<SignInRoute> {
            val viewModel: SignInViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.signedIn.collect {
                    navController.navigate(AccountRoute) { popUpTo<SignInRoute> { inclusive = true } }
                }
            }
            SignInScreen(
                state = state,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onShortLivedChange = viewModel::onShortLivedChange,
                onFillDemo = viewModel::fillDemo,
                onSubmit = viewModel::submit,
                onBack = back,
            )
        }
        composable<AccountRoute> {
            val viewModel: AccountViewModel = hiltViewModel()
            val session by viewModel.session.collectAsStateWithLifecycle()
            val profile by viewModel.profile.collectAsStateWithLifecycle()
            val signingOut by viewModel.signingOut.collectAsStateWithLifecycle()
            val loaded by viewModel.loaded.collectAsStateWithLifecycle()
            val current = session
            LaunchedEffect(viewModel) {
                viewModel.signedOut.collect { navController.popBackStack(ProfileRoute, inclusive = false) }
            }
            // A session lost without the user asking (expiry) is replaced by sign-in; a deliberate sign-out returns to Profile.
            // Before the stored session is read (Account restored after process death) there is nothing to judge yet.
            LaunchedEffect(loaded, current == null, signingOut) {
                if (loaded && current == null && !signingOut) {
                    navController.navigate(SignInRoute) { popUpTo<AccountRoute> { inclusive = true } }
                }
            }
            if (current != null) {
                AccountScreen(
                    session = current,
                    profile = profile,
                    onLoadProfile = viewModel::loadProfile,
                    onSignOut = viewModel::signOut,
                    onBack = back,
                )
            }
        }
    }
}
