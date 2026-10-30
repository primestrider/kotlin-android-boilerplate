package com.primestrider.kotlinstarter.feature.example.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.AppButton
import com.primestrider.kotlinstarter.core.designsystem.component.AppScaffold
import com.primestrider.kotlinstarter.core.designsystem.component.ButtonVariant
import com.primestrider.kotlinstarter.core.designsystem.component.EmptyState
import com.primestrider.kotlinstarter.core.designsystem.component.ErrorState
import com.primestrider.kotlinstarter.core.designsystem.component.SearchField
import com.primestrider.kotlinstarter.core.designsystem.component.Skeleton
import com.primestrider.kotlinstarter.core.designsystem.theme.Radii
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.core.network.asApiError
import com.primestrider.kotlinstarter.core.network.userMessage
import java.util.Locale

private const val SKELETON_ROWS = 6

/** Searchable, infinitely scrolling product list; every load state maps to a state component. */
@Composable
fun ProductsScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    products: LazyPagingItems<ProductDto>,
    onOpen: (Long) -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(title = stringResource(R.string.products_title), onBack = onBack) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.padding(horizontal = Spacing.s4), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                Text(
                    stringResource(R.string.products_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SearchField(query, onQueryChange, placeholder = stringResource(R.string.products_search_placeholder))
            }
            val refresh = products.loadState.refresh
            PullToRefreshBox(
                isRefreshing = refresh is LoadState.Loading && products.itemCount > 0,
                onRefresh = products::refresh,
                modifier = Modifier.fillMaxSize().padding(top = Spacing.s2),
            ) {
                when {
                    products.itemCount > 0 -> ProductList(products, onOpen)
                    refresh is LoadState.Loading -> SkeletonList()
                    refresh is LoadState.Error ->
                        ErrorState(
                            message = refresh.error.asApiError().userMessage(),
                            onRetry = products::retry,
                            title = stringResource(R.string.products_error_title),
                        )
                    else ->
                        EmptyState(
                            title = stringResource(R.string.products_empty_title),
                            description = stringResource(R.string.products_empty_description),
                        )
                }
            }
        }
    }
}

@Composable
private fun ProductList(
    products: LazyPagingItems<ProductDto>,
    onOpen: (Long) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(count = products.itemCount, key = products.itemKey { it.id }) { index ->
            products[index]?.let { ProductRow(it, onOpen) }
        }
        when (val append = products.loadState.append) {
            is LoadState.Loading ->
                item {
                    Box(Modifier.fillMaxWidth().padding(Spacing.s4), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            is LoadState.Error ->
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = Spacing.s4, vertical = Spacing.s2),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            append.error.asApiError().userMessage(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f),
                        )
                        AppButton(stringResource(R.string.common_action_retry), onClick = products::retry, variant = ButtonVariant.TEXT)
                    }
                }
            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun ProductRow(
    product: ProductDto,
    onOpen: (Long) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onOpen(product.id) }
            .padding(horizontal = Spacing.s4, vertical = Spacing.s2),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = product.thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(Spacing.s16).clip(RoundedCornerShape(Radii.md)),
        )
        Column(Modifier.weight(1f)) {
            Text(product.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                product.brand ?: product.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Temporary: Task 2 replaces this with the locale-aware currency formatter.
            Text(String.format(Locale.US, "$%.2f", product.price), style = MaterialTheme.typography.bodyMedium)
        }
        if (product.stock > 0) {
            Text(
                stringResource(R.string.products_stock, product.stock),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                stringResource(R.string.products_out_of_stock),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun SkeletonList() {
    Column(Modifier.fillMaxSize()) {
        repeat(SKELETON_ROWS) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = Spacing.s4, vertical = Spacing.s2),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Skeleton(Modifier.size(Spacing.s16), RoundedCornerShape(Radii.md))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s1_5)) {
                    Skeleton(Modifier.fillMaxWidth(0.7f).height(Spacing.s4))
                    Skeleton(Modifier.fillMaxWidth(0.4f).height(Spacing.s3))
                    Skeleton(Modifier.fillMaxWidth(0.25f).height(Spacing.s3))
                }
            }
        }
    }
}
