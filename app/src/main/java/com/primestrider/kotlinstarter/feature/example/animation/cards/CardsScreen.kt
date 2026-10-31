package com.primestrider.kotlinstarter.feature.example.animation.cards

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import com.primestrider.kotlinstarter.feature.example.animation.AnimExample
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private val sampleCards =
    listOf("Mountain Trip", "City Lights", "Ocean Days", "Forest Walk", "Desert Road")
        .mapIndexed { index, title -> DemoCard(index, title, index) }

private val cardShape = RoundedCornerShape(16.dp)

/** Card motion: press feedback, a scaling carousel, expand, swipe to archive with undo, and a flip. */
@Composable
fun CardsScreen(onBack: () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    ExamplePageScaffold(
        R.string.anim_cards_title,
        R.string.anim_cards_subtitle,
        onBack,
        snackbarHost = { SnackbarHost(snackbar) },
    ) {
        PressExample()
        CarouselExample()
        ExpandExample()
        SwipeExample(snackbar)
        FlipExample()
    }
}

/** Gradient and the text colour that is readable on it, both from theme roles. */
private class CardLook(
    val brush: Brush,
    val onColor: Color,
)

@Composable
private fun cardLook(colorIndex: Int): CardLook {
    val c = MaterialTheme.colorScheme
    val looks =
        listOf(
            CardLook(Brush.linearGradient(listOf(c.primary, c.tertiary)), c.onPrimary),
            CardLook(Brush.linearGradient(listOf(c.secondary, c.primary)), c.onPrimary),
            CardLook(Brush.linearGradient(listOf(c.tertiary, c.secondary)), c.onPrimary),
            CardLook(Brush.linearGradient(listOf(c.primaryContainer, c.tertiaryContainer)), c.onPrimaryContainer),
            CardLook(Brush.linearGradient(listOf(c.secondaryContainer, c.primaryContainer)), c.onPrimaryContainer),
        )
    return looks[colorIndex.mod(looks.size)]
}

@Composable
private fun PressExample() {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.96f else 1f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "press",
    )
    AnimExample(R.string.anim_cards_press_title, R.string.anim_cards_press_usage, "collectIsPressedAsState + spring()") {
        Card(
            onClick = {},
            interactionSource = source,
            modifier =
                Modifier.fillMaxWidth().heightIn(min = 72.dp).graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            Text(stringResource(R.string.anim_cards_press_label), Modifier.padding(Spacing.s4))
        }
    }
}

@Composable
private fun CarouselExample() {
    val pagerState = rememberPagerState { sampleCards.size }
    AnimExample(R.string.anim_cards_carousel_title, R.string.anim_cards_carousel_usage, "HorizontalPager + graphicsLayer") {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 56.dp),
            pageSpacing = Spacing.s2,
        ) { page ->
            val look = cardLook(sampleCards[page].colorIndex)
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
                    .graphicsLayer {
                        val offset = pagerState.getOffsetDistanceInPages(page).absoluteValue
                        val s = carouselScale(offset)
                        scaleX = s
                        scaleY = s
                        alpha = carouselAlpha(offset)
                    }.clip(cardShape)
                    .background(look.brush)
                    .padding(Spacing.s4),
                contentAlignment = Alignment.BottomStart,
            ) {
                Text(sampleCards[page].title, style = MaterialTheme.typography.titleLarge, color = look.onColor)
            }
        }
        Text(
            stringResource(R.string.anim_cards_carousel_position, pagerState.currentPage + 1, sampleCards.size),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Composable
private fun ExpandExample() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevron")
    val stateText = stringResource(if (expanded) R.string.anim_cards_expanded else R.string.anim_cards_collapsed)
    AnimExample(R.string.anim_cards_expand_title, R.string.anim_cards_expand_usage, "Modifier.animateContentSize()") {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .semantics { stateDescription = stateText }
                    .clickable(role = Role.Button) { expanded = !expanded },
        ) {
            Column(Modifier.padding(horizontal = Spacing.s4)) {
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stringResource(R.string.anim_cards_expand_header), style = MaterialTheme.typography.titleMedium)
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { rotationZ = rotation },
                    )
                }
                if (expanded) {
                    Text(
                        stringResource(R.string.anim_cards_expand_body),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = Spacing.s4),
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeExample(snackbar: SnackbarHostState) {
    val archive = rememberSaveable(saver = ArchiveState.Saver) { ArchiveState(sampleCards) }
    val scope = rememberCoroutineScope()
    val archivedMessage = stringResource(R.string.anim_cards_archived)
    val undoLabel = stringResource(R.string.anim_cards_undo)

    fun archiveCard(card: DemoCard) {
        archive.archive(card.id)
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result =
                snackbar.showSnackbar(
                    message = archivedMessage.format(card.title),
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Long,
                )
            if (result == SnackbarResult.ActionPerformed) archive.undo()
        }
    }

    AnimExample(
        R.string.anim_cards_swipe_title,
        R.string.anim_cards_swipe_usage,
        "SwipeToDismissBox + rememberSwipeToDismissBoxState",
    ) {
        if (archive.cards.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.s2),
            ) {
                Text(stringResource(R.string.anim_cards_empty), style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { archive.reset() }) { Text(stringResource(R.string.anim_cards_reset)) }
            }
        }
        Column(
            Modifier.fillMaxWidth().animateContentSize().testTag("swipeList"),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            archive.cards.forEach { card ->
                key(card.id) { SwipeCard(card, onArchive = { archiveCard(card) }) }
            }
        }
    }
}

@Composable
private fun SwipeCard(
    card: DemoCard,
    onArchive: () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState()
    val look = cardLook(card.colorIndex)
    SwipeToDismissBox(
        state = state,
        onDismiss = { onArchive() },
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(cardShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(horizontal = Spacing.s4),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(R.string.anim_cards_archive_hint), color = MaterialTheme.colorScheme.onTertiaryContainer)
            }
        },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(look.brush)
                .padding(start = Spacing.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                card.title,
                style = MaterialTheme.typography.titleMedium,
                color = look.onColor,
                modifier = Modifier.weight(1f).padding(vertical = Spacing.s4),
            )
            IconButton(onClick = onArchive) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = stringResource(R.string.anim_cards_archive_action, card.title),
                    tint = look.onColor,
                )
            }
        }
    }
}

@Composable
private fun FlipExample() {
    var flipped by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, spring(Spring.DampingRatioLowBouncy), label = "flip")
    val density = LocalDensity.current.density
    val stateText = stringResource(if (flipped) R.string.anim_cards_showing_back else R.string.anim_cards_showing_front)
    val showBack = rotation > 90f
    val look = cardLook(if (showBack) 4 else 0)
    AnimExample(R.string.anim_cards_flip_title, R.string.anim_cards_flip_usage, "graphicsLayer { rotationY; cameraDistance }") {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }.clip(cardShape)
                .background(look.brush)
                .semantics { stateDescription = stateText }
                .clickable(role = Role.Button) { flipped = !flipped }
                .padding(Spacing.s4),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(if (showBack) R.string.anim_cards_flip_back else R.string.anim_cards_flip_front),
                style = MaterialTheme.typography.titleLarge,
                color = look.onColor,
                // Past 90 degrees the whole layer is mirrored, so turn the text back the right way round.
                modifier = Modifier.graphicsLayer { if (showBack) rotationY = 180f },
            )
        }
        FilledTonalButton(onClick = { flipped = !flipped }, modifier = Modifier.heightIn(min = 48.dp)) {
            Text(stringResource(R.string.anim_cards_flip_button))
        }
    }
}
