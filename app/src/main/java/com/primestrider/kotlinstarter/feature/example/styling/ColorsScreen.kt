package com.primestrider.kotlinstarter.feature.example.styling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.AppColors
import com.primestrider.kotlinstarter.core.designsystem.theme.AppThemeTokens
import com.primestrider.kotlinstarter.core.designsystem.theme.Palette
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

/** Semantic token names paired with the active theme's values. */
internal fun semanticColorEntries(colors: AppColors): List<Pair<String, Color>> =
    listOf(
        "background" to colors.background,
        "foreground" to colors.foreground,
        "muted" to colors.muted,
        "mutedForeground" to colors.mutedForeground,
        "border" to colors.border,
        "card" to colors.card,
        "cardForeground" to colors.cardForeground,
        "primary" to colors.primary,
        "primaryForeground" to colors.primaryForeground,
        "secondary" to colors.secondary,
        "secondaryForeground" to colors.secondaryForeground,
        "destructive" to colors.destructive,
        "destructiveForeground" to colors.destructiveForeground,
        "success" to colors.success,
        "warning" to colors.warning,
        "info" to colors.info,
    )

@Composable
fun ColorsScreen(onBack: () -> Unit) {
    StylingScaffold(
        title = R.string.example_styling_colors_title,
        subtitle = R.string.example_styling_colors_subtitle,
        onBack = onBack,
    ) {
        StylingSection(R.string.styling_section_semantic_colors) {
            semanticColorEntries(AppThemeTokens.colors).forEach { (name, color) ->
                ColorSwatchRow(name, color)
            }
        }
        StylingSection(R.string.styling_section_palette) {
            Palette.scales.forEach { (scale, steps) ->
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.s1)) {
                    Text(scale, style = MaterialTheme.typography.labelLarge)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        steps.forEach { (step, color) -> PaletteStep(step, color) }
                    }
                }
            }
        }
    }
}
