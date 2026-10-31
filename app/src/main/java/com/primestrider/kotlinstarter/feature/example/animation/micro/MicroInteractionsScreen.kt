package com.primestrider.kotlinstarter.feature.example.animation.micro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import com.primestrider.kotlinstarter.feature.example.animation.AnimExample
import kotlinx.coroutines.launch

/** Small feedback animations: like pop, sliding counter, tab indicator and a content swap. */
@Composable
fun MicroInteractionsScreen(onBack: () -> Unit) {
    ExamplePageScaffold(R.string.anim_micro_title, R.string.anim_micro_subtitle, onBack) {
        LikeExample()
        CounterExample()
        TabIndicatorExample()
        ContentChangeExample()
    }
}

@Composable
private fun LikeExample() {
    var liked by rememberSaveable { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val tint by animateColorAsState(
        if (liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "likeTint",
    )
    AnimExample(R.string.anim_micro_like_title, R.string.anim_micro_like_usage, "Animatable + spring()") {
        IconButton(
            onClick = {
                liked = !liked
                scope.launch {
                    scale.animateTo(1.3f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                    scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                }
            },
        ) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = stringResource(if (liked) R.string.anim_micro_liked else R.string.anim_micro_like),
                tint = tint,
                modifier =
                    Modifier.graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    },
            )
        }
    }
}

@Composable
private fun CounterExample() {
    var count by rememberSaveable { mutableIntStateOf(0) }
    val increase = stringResource(R.string.anim_micro_increase)
    val decrease = stringResource(R.string.anim_micro_decrease)
    AnimExample(R.string.anim_micro_counter_title, R.string.anim_micro_counter_usage, "AnimatedContent") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            FilledTonalButton(onClick = { count-- }, modifier = Modifier.semantics { contentDescription = decrease }) {
                Text("−")
            }
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    val up = targetState > initialState
                    (slideInVertically { if (up) it else -it } + fadeIn()) togetherWith
                        (slideOutVertically { if (up) -it else it } + fadeOut())
                },
                label = "counter",
            ) { value ->
                Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
            }
            FilledTonalButton(onClick = { count++ }, modifier = Modifier.semantics { contentDescription = increase }) {
                Text("+")
            }
        }
    }
}

@Composable
private fun TabIndicatorExample() {
    val labels = listOf(R.string.anim_micro_tab_photos, R.string.anim_micro_tab_videos, R.string.anim_micro_tab_albums)
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val xs = remember { mutableStateListOf(0, 0, 0) }
    val widths = remember { mutableStateListOf(0, 0, 0) }
    val density = LocalDensity.current
    val offset by animateDpAsState(with(density) { xs[selected].toDp() }, label = "tabOffset")
    val width by animateDpAsState(with(density) { widths[selected].toDp() }, label = "tabWidth")
    AnimExample(R.string.anim_micro_tabs_title, R.string.anim_micro_tabs_usage, "animateDpAsState") {
        Box(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                labels.forEachIndexed { index, label ->
                    Text(
                        stringResource(label),
                        style = MaterialTheme.typography.titleSmall,
                        color =
                            if (index == selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        modifier =
                            Modifier
                                .onGloballyPositioned {
                                    xs[index] = it.positionInParent().x.toInt()
                                    widths[index] = it.size.width
                                }.clickable { selected = index }
                                .padding(vertical = Spacing.s2, horizontal = Spacing.s3),
                    )
                }
            }
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = offset)
                    .width(width)
                    .height(3.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun ContentChangeExample() {
    val states = listOf(R.string.anim_micro_status_pending, R.string.anim_micro_status_paid, R.string.anim_micro_status_shipped)
    var index by rememberSaveable { mutableIntStateOf(0) }
    val next = stringResource(R.string.anim_micro_next_status)
    AnimExample(R.string.anim_micro_content_title, R.string.anim_micro_content_usage, "AnimatedContent + togetherWith") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn()) togetherWith (slideOutVertically { -it / 2 } + fadeOut())
                },
                label = "status",
            ) { value ->
                Text(
                    stringResource(states[value]),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = Spacing.s4, vertical = Spacing.s1),
                )
            }
            FilledTonalButton(
                onClick = { index = (index + 1) % states.size },
                modifier = Modifier.semantics { contentDescription = next },
            ) { Text(stringResource(R.string.anim_micro_next)) }
        }
    }
}
