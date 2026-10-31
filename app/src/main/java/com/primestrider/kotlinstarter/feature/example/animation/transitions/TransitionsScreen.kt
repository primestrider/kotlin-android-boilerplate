package com.primestrider.kotlinstarter.feature.example.animation.transitions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import com.primestrider.kotlinstarter.feature.example.animation.AnimExample

/** Height of the demo frame: tall enough for the three cards and the detail page. */
private val DemoFrameHeight = 340.dp

/**
 * Page transitions: a nested NavHost demo that plays the chosen Material pattern, one card per pattern saying when
 * to use it, and a predictive back example.
 *
 * Back handling, one owner per press: while a demo detail is open the nested NavHost's own handler pops it (and
 * follows the predictive back gesture); on the demo list the [BackHandler] below calls [onBack]. Their enabled
 * states never overlap.
 */
@Composable
fun TransitionsScreen(onBack: () -> Unit) {
    val pattern = rememberSaveable { mutableStateOf(TransitionPattern.CONTAINER_TRANSFORM) }
    val demoNav = rememberNavController()
    val demoStack by demoNav.currentBackStack.collectAsState()
    val detailOpen = demoStack.lastOrNull()?.destination?.hasRoute<DemoDetailRoute>() == true
    BackHandler(enabled = !detailOpen) {
        // Decided at press time: right after "Open" the stack can hold a detail before this state catches up.
        if (demoNav.currentBackStackEntry?.destination?.hasRoute<DemoDetailRoute>() == true) {
            demoNav.popBackStack()
        } else {
            onBack()
        }
    }
    ExamplePageScaffold(R.string.anim_transitions_title, R.string.anim_transitions_subtitle, onBack) {
        AnimExample(
            R.string.anim_transitions_demo_title,
            R.string.anim_transitions_demo_usage,
            "NavHost + composable(enterTransition, popExitTransition)",
        ) {
            PatternPicker(pattern)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(DemoFrameHeight)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                DemoNavHost(demoNav, pattern, Modifier.fillMaxSize())
            }
        }
        TransitionPattern.entries.forEach { AnimExample(it.label, it.usage, it.api) {} }
        PredictiveBackDemo()
    }
}

@Composable
private fun PatternPicker(pattern: MutableState<TransitionPattern>) {
    FlowRow(
        Modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        TransitionPattern.entries.forEach { option ->
            val selected = option == pattern.value
            FilterChip(
                selected = selected,
                onClick = { pattern.value = option },
                label = { Text(stringResource(option.label)) },
                leadingIcon =
                    if (selected) {
                        { Icon(Icons.Filled.Check, contentDescription = null, Modifier.size(FilterChipDefaults.IconSize)) }
                    } else {
                        null
                    },
            )
        }
    }
}
