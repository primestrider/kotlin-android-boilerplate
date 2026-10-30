package com.primestrider.kotlinstarter.feature.example.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.AppThemeTokens
import com.primestrider.kotlinstarter.core.designsystem.theme.Radii
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.DemoSection
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import com.primestrider.kotlinstarter.feature.example.rememberSaveableStateList
import kotlin.math.roundToInt

internal const val PROGRESS_BAR_TAG = "display_progress"
internal const val LONG_ROW_SWITCH_TAG = "display_long_row_switch"
internal const val LONG_HEADLINE =
    "A very long row title that has to truncate rather than shove the trailing slot off screen"

private const val PROGRESS_STEP = 0.1f
private const val BADGE_MAX = 99

/** First letter of the first two words, uppercased; `"?"` when the name is blank. */
internal fun initialsOf(name: String): String =
    name
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

/** Material 3 display components, plus avatar / skeleton / empty-state / banner patterns M3 does not ship. */
@Composable
fun DisplayScreen(onBack: () -> Unit) {
    ExamplePageScaffold(
        title = R.string.example_components_display_title,
        subtitle = R.string.example_components_display_subtitle,
        onBack = onBack,
    ) {
        CardsSection()
        ListItemsSection()
        BadgesSection()
        DividersSection()
        ProgressSection()
        AvatarsSection()
        SkeletonSection()
        EmptyStateSection()
        BannersSection()
    }
}

@Composable
private fun CardsSection() {
    var taps by rememberSaveable { mutableIntStateOf(0) }
    DemoSection(R.string.components_section_cards) {
        Card(Modifier.fillMaxWidth()) { CardText(R.string.components_card_filled) }
        ElevatedCard(Modifier.fillMaxWidth()) { CardText(R.string.components_card_elevated) }
        OutlinedCard(Modifier.fillMaxWidth()) { CardText(R.string.components_card_outlined) }
        OutlinedCard(onClick = { taps++ }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.components_card_clickable, taps), Modifier.padding(Spacing.s4))
        }
    }
}

@Composable
private fun CardText(
    @StringRes title: Int,
) {
    Column(Modifier.padding(Spacing.s4), verticalArrangement = Arrangement.spacedBy(Spacing.s1)) {
        Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.components_card_body), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ListItemsSection() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    DemoSection(R.string.components_section_list_items) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.components_list_profile)) },
            supportingContent = { Text(stringResource(R.string.components_list_profile_supporting)) },
            leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text(LONG_HEADLINE, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            leadingContent = { Icon(Icons.Filled.Notifications, contentDescription = null) },
            trailingContent = {
                Switch(checked = enabled, onCheckedChange = { enabled = it }, modifier = Modifier.testTag(LONG_ROW_SWITCH_TAG))
            },
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text(stringResource(R.string.components_list_sign_out)) },
            colors = ListItemDefaults.colors(headlineColor = MaterialTheme.colorScheme.error),
        )
    }
}

@Composable
private fun BadgesSection() {
    val count = 120
    DemoSection(R.string.components_section_badges) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s6), verticalAlignment = Alignment.CenterVertically) {
            Badge()
            Badge { Text("8") }
            BadgedBox(badge = { Badge { Text(if (count > BADGE_MAX) "$BADGE_MAX+" else "$count") } }) {
                Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.components_cd_notifications))
            }
            BadgedBox(badge = { Badge() }) {
                Icon(Icons.Filled.Notifications, contentDescription = stringResource(R.string.components_cd_notifications))
            }
        }
    }
}

@Composable
private fun DividersSection() {
    DemoSection(R.string.components_section_dividers) {
        HorizontalDivider()
        HorizontalDivider(Modifier.padding(start = Spacing.s12))
        Row(Modifier.height(Spacing.s8), verticalAlignment = Alignment.CenterVertically) {
            Text("A")
            VerticalDivider(Modifier.padding(horizontal = Spacing.s3))
            Text("B")
            VerticalDivider(Modifier.padding(horizontal = Spacing.s3))
            Text("C")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f))
            Text(
                stringResource(R.string.components_divider_label),
                Modifier.padding(horizontal = Spacing.s3),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ProgressSection() {
    var progress by rememberSaveable { mutableFloatStateOf(0.4f) }
    DemoSection(R.string.components_section_progress) {
        Text(stringResource(R.string.components_progress_value, (progress * 100).roundToInt()))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().testTag(PROGRESS_BAR_TAG))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            OutlinedButton(onClick = { progress = (progress - PROGRESS_STEP).coerceIn(0f, 1f) }) { Text("−10%") }
            OutlinedButton(onClick = { progress = (progress + PROGRESS_STEP).coerceIn(0f, 1f) }) { Text("+10%") }
        }
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s4), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(progress = { progress })
            CircularProgressIndicator(Modifier.size(Spacing.s4), strokeWidth = Spacing.s0_5)
            CircularProgressIndicator(Modifier.size(Spacing.s8))
            CircularProgressIndicator(Modifier.size(Spacing.s12))
        }
    }
}

@Composable
private fun AvatarsSection() {
    val names = listOf("Ada Lovelace", "Grace Hopper", "Alan Turing", "Margaret Hamilton", "Linus Torvalds", "Barbara Liskov")
    DemoSection(R.string.components_section_avatars) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s4), verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(initialsOf("Ada Lovelace"), Spacing.s8, CircleShape)
            InitialsAvatar(initialsOf("Grace Hopper"), Spacing.s12, CircleShape)
            InitialsAvatar(initialsOf("Alan Turing"), Spacing.s12, RoundedCornerShape(Radii.md))
            Box {
                InitialsAvatar(initialsOf("Margaret Hamilton"), Spacing.s12, CircleShape)
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(Spacing.s3)
                        .background(AppThemeTokens.colors.success, CircleShape)
                        .border(Spacing.s0_5, MaterialTheme.colorScheme.surface, CircleShape),
                )
            }
        }
        val visible = 4
        Row {
            names.take(visible).forEachIndexed { index, name ->
                InitialsAvatar(
                    initialsOf(name),
                    Spacing.s10,
                    CircleShape,
                    Modifier
                        .offset(x = -Spacing.s3 * index)
                        .border(Spacing.s0_5, MaterialTheme.colorScheme.surface, CircleShape),
                )
            }
            InitialsAvatar("+${names.size - visible}", Spacing.s10, CircleShape, Modifier.offset(x = -Spacing.s3 * visible))
        }
    }
}

@Composable
private fun InitialsAvatar(
    label: String,
    size: Dp,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primaryContainer, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun SkeletonSection() {
    var loaded by rememberSaveable { mutableStateOf(false) }
    val pulse by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 800), RepeatMode.Reverse),
        label = "skeleton-alpha",
    )
    DemoSection(R.string.components_section_skeleton) {
        if (loaded) {
            Text(stringResource(R.string.components_skeleton_content))
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s3), modifier = Modifier.alpha(pulse)) {
                SkeletonBlock(Modifier.size(Spacing.s12), CircleShape)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                    SkeletonBlock(Modifier.fillMaxWidth().height(Spacing.s4))
                    SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(Spacing.s4))
                }
            }
        }
        TextButton(onClick = {
            loaded = !loaded
        }) { Text(stringResource(if (loaded) R.string.components_skeleton_show else R.string.components_skeleton_loaded)) }
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(Radii.sm),
) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh, shape))
}

@Composable
private fun EmptyStateSection() {
    DemoSection(R.string.components_section_empty) {
        Column(
            Modifier.fillMaxWidth().padding(Spacing.s6),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Icon(
                Icons.Filled.Email,
                contentDescription = null,
                modifier = Modifier.size(Spacing.s12),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(stringResource(R.string.components_empty_title), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.components_empty_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = {}) { Text(stringResource(R.string.components_empty_action)) }
        }
    }
}

private data class BannerSpec(
    @StringRes val title: Int,
    @StringRes val body: Int,
    val icon: ImageVector,
)

@Composable
private fun BannersSection() {
    val colors = AppThemeTokens.colors
    val banners =
        listOf(
            BannerSpec(R.string.components_banner_info_title, R.string.components_banner_info_body, Icons.Filled.Info) to colors.info,
            BannerSpec(R.string.components_banner_success_title, R.string.components_banner_success_body, Icons.Filled.CheckCircle) to
                colors.success,
            BannerSpec(R.string.components_banner_warning_title, R.string.components_banner_warning_body, Icons.Filled.Warning) to
                colors.warning,
            BannerSpec(R.string.components_banner_error_title, R.string.components_banner_error_body, Icons.Filled.Warning) to
                colors.destructive,
        )
    val visible = rememberSaveableStateList(true, true, true, true)
    DemoSection(R.string.components_section_banners) {
        banners.forEachIndexed { index, (spec, accent) ->
            if (visible[index]) Banner(spec, accent, onDismiss = { visible[index] = false })
        }
    }
}

@Composable
private fun Banner(
    spec: BannerSpec,
    accent: Color,
    onDismiss: () -> Unit,
) {
    val title = stringResource(spec.title)
    Surface(
        shape = RoundedCornerShape(Radii.lg),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, accent),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(Spacing.s3), horizontalArrangement = Arrangement.spacedBy(Spacing.s3)) {
            Icon(spec.icon, contentDescription = null, tint = accent)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(stringResource(spec.body), style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.components_cd_dismiss, title))
            }
        }
    }
}
