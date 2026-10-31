package com.primestrider.kotlinstarter.feature.example

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.EmptyState
import com.primestrider.kotlinstarter.core.designsystem.component.SearchField
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

internal const val HOME_LIST_TAG = "home_list"

private const val ALL_SECTIONS = -1

/**
 * A titled list of example pages grouped in section cards. With more than one section it adds a search box
 * (titles and subtitles, in the app language) and a chip per section; both survive recreation.
 */
@Composable
fun HomeScreen(
    @StringRes title: Int = R.string.app_name,
    sections: List<ExampleSection>,
    onOpen: (Any) -> Unit,
) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableIntStateOf(ALL_SECTIONS) }
    val filters = sections.size > 1
    val shown = filterSections(sections, query, selected.takeIf { it != ALL_SECTIONS }) { context.getString(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().testTag(HOME_LIST_TAG),
        contentPadding = PaddingValues(Spacing.s4),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        item {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
        }
        if (filters) {
            item {
                SearchField(
                    query = query,
                    onQueryChange = { query = it },
                    placeholder = stringResource(R.string.home_search_placeholder),
                )
            }
            item { SectionChips(sections, selected, onSelect = { selected = it }) }
        }
        items(shown, key = { it.title }) { section -> SectionCard(section, onOpen) }
        if (shown.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.home_no_match, query.trim()),
                    description = stringResource(R.string.home_no_match_hint),
                    icon = Icons.Filled.Search,
                    actionLabel = stringResource(R.string.component_clear_search),
                    onAction = {
                        query = ""
                        selected = ALL_SECTIONS
                    },
                    modifier = Modifier.padding(top = Spacing.s8),
                )
            }
        }
    }
}

/** "All" plus one chip per section; picking one shows only that section. */
@Composable
private fun SectionChips(
    sections: List<ExampleSection>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        Chip(stringResource(R.string.home_filter_all), selected == ALL_SECTIONS, icon = null) { onSelect(ALL_SECTIONS) }
        sections.forEachIndexed { index, section ->
            Chip(stringResource(section.title), selected == index, section.icon) { onSelect(index) }
        }
    }
}

@Composable
private fun Chip(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = icon?.let { { Icon(it, contentDescription = null, modifier = Modifier.size(18.dp)) } },
        modifier = Modifier.semantics { this.selected = isSelected },
    )
}

/** One section: an icon, title, description and example count, then its entries as rows. */
@Composable
private fun SectionCard(
    section: ExampleSection,
    onOpen: (Any) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth(),
    ) {
        SectionHeader(section)
        section.entries.forEachIndexed { index, entry ->
            HorizontalDivider(
                modifier = Modifier.padding(start = if (index == 0) 0.dp else Spacing.s4),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            EntryRow(entry, onOpen)
        }
    }
}

@Composable
private fun SectionHeader(section: ExampleSection) {
    val count = stringResource(R.string.home_section_count, section.entries.size)
    Row(
        Modifier.fillMaxWidth().padding(Spacing.s4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s3),
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(section.title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                stringResource(section.description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
            Text(
                section.entries.size.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier =
                    Modifier
                        .padding(horizontal = Spacing.s2, vertical = Spacing.s1)
                        .clearAndSetSemantics { contentDescription = count },
            )
        }
    }
}

@Composable
private fun EntryRow(
    entry: ExampleEntry,
    onOpen: (Any) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onOpen(entry.route) }
            .padding(horizontal = Spacing.s4, vertical = Spacing.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(entry.title), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(entry.subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
