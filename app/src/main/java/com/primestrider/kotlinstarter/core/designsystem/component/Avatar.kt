package com.primestrider.kotlinstarter.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.AppThemeTokens

enum class AvatarSize(
    val dp: Dp,
) {
    SMALL(32.dp),
    MEDIUM(40.dp),
    LARGE(48.dp),
    XLARGE(64.dp),
}

enum class AvatarStatus(
    @StringRes val label: Int,
) {
    ONLINE(R.string.component_status_online),
    OFFLINE(R.string.component_status_offline),
    BUSY(R.string.component_status_busy),
    AWAY(R.string.component_status_away),
}

/** First letter of the first two words, uppercased; `"?"` when the name is blank. */
fun initialsOf(name: String): String =
    name
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

/** Initials avatar with an optional presence dot; announced as the name (plus status). */
@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.MEDIUM,
    shape: Shape = CircleShape,
    status: AvatarStatus? = null,
) {
    val description = status?.let { "$name, ${stringResource(it.label)}" } ?: name
    Box(modifier.semantics(mergeDescendants = true) { contentDescription = description }) {
        AvatarCircle(initialsOf(name), size, shape)
        if (status != null) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(size.dp / 4)
                    .background(status.color(), CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            )
        }
    }
}

/** Overlapping avatars; names beyond [max] collapse into a "+N" chip. */
@Composable
fun AvatarGroup(
    names: List<String>,
    modifier: Modifier = Modifier,
    max: Int = 4,
    size: AvatarSize = AvatarSize.MEDIUM,
) {
    val overlap = size.dp / 4
    val shown = names.take(max)
    val ring = Modifier.border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
    Row(modifier) {
        shown.forEachIndexed { index, name ->
            Avatar(name, ring.offset(x = -overlap * index), size)
        }
        val hidden = names.size - shown.size
        if (hidden > 0) {
            AvatarCircle("+$hidden", size, CircleShape, ring.offset(x = -overlap * shown.size))
        }
    }
}

@Composable
private fun AvatarCircle(
    label: String,
    size: AvatarSize,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.size(size.dp).background(MaterialTheme.colorScheme.primaryContainer, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun AvatarStatus.color(): Color =
    when (this) {
        AvatarStatus.ONLINE -> AppThemeTokens.colors.success
        AvatarStatus.OFFLINE -> MaterialTheme.colorScheme.outline
        AvatarStatus.BUSY -> MaterialTheme.colorScheme.error
        AvatarStatus.AWAY -> AppThemeTokens.colors.warning
    }
