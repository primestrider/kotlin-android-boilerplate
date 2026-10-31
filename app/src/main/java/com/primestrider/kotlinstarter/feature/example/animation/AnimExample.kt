package com.primestrider.kotlinstarter.feature.example.animation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.DemoSection

/** One live animation as a [DemoSection]: when to use it (muted), the Compose [api] behind it, then [content]. */
@Composable
fun AnimExample(
    @StringRes title: Int,
    @StringRes usage: Int,
    api: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        DemoSection(title) {
            Text(
                stringResource(usage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                api,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
            )
            Column(
                Modifier.fillMaxWidth().padding(top = Spacing.s1),
                verticalArrangement = Arrangement.spacedBy(Spacing.s3),
            ) {
                content()
            }
        }
    }
}
