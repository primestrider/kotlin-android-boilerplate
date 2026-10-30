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
import androidx.paging.compose.collectAsLazyPagingItems
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.feature.example.AccountRoute
import com.primestrider.kotlinstarter.feature.example.ActionsRoute
import com.primestrider.kotlinstarter.feature.example.AppearanceRoute
import com.primestrider.kotlinstarter.feature.example.ColorsRoute
import com.primestrider.kotlinstarter.feature.example.ComponentsHomeRoute
import com.primestrider.kotlinstarter.feature.example.ComponentsHomeScreen
import com.primestrider.kotlinstarter.feature.example.DisplayRoute
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import com.primestrider.kotlinstarter.feature.example.FeedbackRoute
import com.primestrider.kotlinstarter.feature.example.FormExampleRoute
import com.primestrider.kotlinstarter.feature.example.HomeScreen
import com.primestrider.kotlinstarter.feature.example.InputsRoute
import com.primestrider.kotlinstarter.feature.example.LayoutRoute
import com.primestrider.kotlinstarter.feature.example.OverlaysRoute
import com.primestrider.kotlinstarter.feature.example.ProductDetailRoute
import com.primestrider.kotlinstarter.feature.example.ProductsRoute
import com.primestrider.kotlinstarter.feature.example.ProfileRoute
import com.primestrider.kotlinstarter.feature.example.SettingsRoute
import com.primestrider.kotlinstarter.feature.example.SignInRoute
import com.primestrider.kotlinstarter.feature.example.SizingRoute
import com.primestrider.kotlinstarter.feature.example.SpacingRoute
import com.primestrider.kotlinstarter.feature.example.ThemeRoute
import com.primestrider.kotlinstarter.feature.example.TypographyRoute
import com.primestrider.kotlinstarter.feature.example.auth.AccountScreen
import com.primestrider.kotlinstarter.feature.example.auth.AccountViewModel
import com.primestrider.kotlinstarter.feature.example.auth.ProfileScreen
import com.primestrider.kotlinstarter.feature.example.auth.SignInScreen
import com.primestrider.kotlinstarter.feature.example.auth.SignInViewModel
import com.primestrider.kotlinstarter.feature.example.componentEntries
import com.primestrider.kotlinstarter.feature.example.components.ActionsScreen
import com.primestrider.kotlinstarter.feature.example.components.DisplayScreen
import com.primestrider.kotlinstarter.feature.example.components.FeedbackScreen
import com.primestrider.kotlinstarter.feature.example.components.FeedbackViewModel
import com.primestrider.kotlinstarter.feature.example.components.InputsScreen
import com.primestrider.kotlinstarter.feature.example.components.OverlaysScreen
import com.primestrider.kotlinstarter.feature.example.exampleSections
import com.primestrider.kotlinstarter.feature.example.form.FormExampleScreen
import com.primestrider.kotlinstarter.feature.example.form.FormExampleViewModel
import com.primestrider.kotlinstarter.feature.example.products.ProductsScreen
import com.primestrider.kotlinstarter.feature.example.products.ProductsViewModel
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

/** One nested graph per bottom-nav tab; each keeps its own back stack. */
@Serializable
data object HomeGraph

@Serializable
data object ComponentsGraph

@Serializable
data object ProfileGraph

/** Home tab: example index, form example and styling pages. Register new Home destinations here. */
fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    val back: () -> Unit = { navController.popBackStack() }
    navigation<HomeGraph>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(sections = exampleSections, onOpen = { route -> navController.navigate(route) })
        }
        composable<ProductsRoute> {
            val viewModel: ProductsViewModel = hiltViewModel()
            val query by viewModel.query.collectAsStateWithLifecycle()
            ProductsScreen(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                products = viewModel.products.collectAsLazyPagingItems(),
                onOpen = { id -> navController.navigate(ProductDetailRoute(id)) },
                onBack = back,
            )
        }
        // Placeholder until the detail screen lands.
        composable<ProductDetailRoute> {
            ExamplePageScaffold(title = R.string.products_title, subtitle = R.string.products_subtitle, onBack = back) {}
        }
        composable<FormExampleRoute> {
            val viewModel: FormExampleViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            FormExampleScreen(
                state = state,
                onChange = viewModel::onChange,
                onSubmit = viewModel::submit,
                onClear = viewModel::clear,
                onBack = back,
            )
        }
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
            ThemeScreen(themeMode = state.themeMode, onThemeModeChange = viewModel::setThemeMode, onBack = back)
        }
    }
}

/** Components tab: the component example pages. */
fun NavGraphBuilder.componentsGraph(navController: NavHostController) {
    val back: () -> Unit = { navController.popBackStack() }
    navigation<ComponentsGraph>(startDestination = ComponentsHomeRoute) {
        composable<ComponentsHomeRoute> {
            ComponentsHomeScreen(entries = componentEntries, onOpen = { route -> navController.navigate(route) })
        }
        composable<ActionsRoute> { ActionsScreen(onBack = back) }
        composable<InputsRoute> { InputsScreen(onBack = back) }
        composable<DisplayRoute> { DisplayScreen(onBack = back) }
        composable<OverlaysRoute> { OverlaysScreen(onBack = back) }
        composable<FeedbackRoute> {
            val viewModel: FeedbackViewModel = hiltViewModel()
            FeedbackScreen(onBack = back, onSaveSucceeded = viewModel::saveSucceeded, onSaveFailed = viewModel::saveFailed)
        }
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
