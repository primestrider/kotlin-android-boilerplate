package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

/** One-of-N segmented control for up to four short options; use [SelectField] for more. */
@Composable
fun <T> SegmentedChoice(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                enabled = enabled,
                label = { Text(label(option), maxLines = 1, overflow = TextOverflow.Ellipsis) },
            )
        }
    }
}

/** Label with a trailing switch; the whole row is the touch target and reports [Role.Switch]. */
@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        AppSwitch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

/** Slider with its [label] and a [valueText] readout above it. */
@Composable
fun LabeledSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    valueText: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(valueText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, steps = steps)
    }
}
