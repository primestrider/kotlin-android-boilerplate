package com.primestrider.kotlinstarter.feature.example

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

internal const val HOME_LIST_TAG = "home_list"

@Composable
fun HomeScreen(
    @StringRes title: Int = R.string.app_name,
    sections: List<ExampleSection>,
    onOpen: (Any) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().testTag(HOME_LIST_TAG),
        contentPadding = PaddingValues(Spacing.s4),
    ) {
        item {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = Spacing.s4),
            )
        }
        sections.forEach { section ->
            item {
                Column(Modifier.padding(top = Spacing.s4, bottom = Spacing.s2)) {
                    Text(stringResource(section.title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(section.description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(section.entries) { entry ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { onOpen(entry.route) }
                        .padding(vertical = Spacing.s3),
                ) {
                    Text(stringResource(entry.title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(entry.subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
