package com.primestrider.kotlinstarter.feature.example.products.ui

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.primestrider.kotlinstarter.feature.example.ProductDetailRoute
import com.primestrider.kotlinstarter.feature.example.ProductsRoute

/** The products list and detail pages. */
fun NavGraphBuilder.productsDestinations(navController: NavController) {
    val back: () -> Unit = { navController.popBackStack() }
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
    composable<ProductDetailRoute> {
        val viewModel: ProductDetailViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        ProductDetailScreen(state = state, onRetry = viewModel::retry, onAddToCart = viewModel::addToCart, onBack = back)
    }
}
