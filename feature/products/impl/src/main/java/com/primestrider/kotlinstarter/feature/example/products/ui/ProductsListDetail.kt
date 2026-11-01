package com.primestrider.kotlinstarter.feature.example.products.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.primestrider.kotlinstarter.core.designsystem.component.EmptyState
import com.primestrider.kotlinstarter.feature.example.products.model.Product
import com.primestrider.kotlinstarter.feature.products.impl.R
import kotlinx.coroutines.launch

/**
 * The products list, with the selected product beside it when the window is wide enough for two panes (tablets,
 * foldables, desktop windows). On narrower windows it is just [ProductsScreen] and a tap calls [onOpenFullScreen], which
 * opens the detail page as before. [detail] draws one product; the caller supplies it so this stays free of ViewModels.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ProductsListDetail(
    query: String,
    onQueryChange: (String) -> Unit,
    products: LazyPagingItems<Product>,
    onOpenFullScreen: (Long) -> Unit,
    onBack: () -> Unit,
    detail: @Composable (id: Long, onClose: () -> Unit) -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()
    if (navigator.scaffoldDirective.maxHorizontalPartitions < 2) {
        ProductsScreen(query = query, onQueryChange = onQueryChange, products = products, onOpen = onOpenFullScreen, onBack = onBack)
        return
    }
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                ProductsScreen(
                    query = query,
                    onQueryChange = onQueryChange,
                    products = products,
                    onOpen = { id -> scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, id) } },
                    onBack = onBack,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val id = navigator.currentDestination?.contentKey
                if (id == null) {
                    EmptyState(
                        title = stringResource(R.string.products_pick_title),
                        description = stringResource(R.string.products_pick_description),
                        icon = Icons.AutoMirrored.Filled.List,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    // A new key gives each product its own ViewModel and scroll state.
                    key(id) { detail(id) { scope.launch { navigator.navigateBack() } } }
                }
            }
        },
    )
}
