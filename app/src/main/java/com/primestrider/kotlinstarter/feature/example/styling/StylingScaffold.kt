package com.primestrider.kotlinstarter.feature.example.styling

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Radii
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

/** Scrollable page with a top app bar (title + back) and a subtitle, shared by all styling pages. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylingScaffold(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.s4),
            verticalArrangement = Arrangement.spacedBy(Spacing.s4),
        ) {
            Text(
                stringResource(subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
        }
    }
}

/** Titled group of demo content inside a styling page. */
@Composable
internal fun StylingSection(
    @StringRes title: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
        Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
        content()
    }
}

/** A color square with its token name. Swatches are the only place raw palette colors are shown. */
@Composable
internal fun ColorSwatchRow(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s3),
    ) {
        Swatch(color)
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun Swatch(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Radii.md)
    Box(
        modifier
            .size(Spacing.s8)
            .background(color, shape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape),
    )
}

/** One palette step: a colored chip with its numeric label, in a row of equal-width chips. */
@Composable
internal fun RowScope.PaletteStep(
    step: Int,
    color: Color,
) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(Spacing.s8).background(color))
        Text(step.toString(), style = MaterialTheme.typography.labelSmall)
    }
}
