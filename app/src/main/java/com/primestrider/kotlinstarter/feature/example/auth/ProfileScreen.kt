package com.primestrider.kotlinstarter.feature.example.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.auth.Session
import com.primestrider.kotlinstarter.core.designsystem.component.AppScaffold
import com.primestrider.kotlinstarter.core.designsystem.component.Avatar
import com.primestrider.kotlinstarter.core.designsystem.component.AvatarSize
import com.primestrider.kotlinstarter.core.designsystem.component.AvatarStatus
import com.primestrider.kotlinstarter.core.designsystem.component.EmptyState
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing

/** Profile tab root: the signed-in user (or a sign-in prompt) and a way into Settings. */
@Composable
fun ProfileScreen(
    session: Session?,
    onSignIn: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    AppScaffold(title = stringResource(R.string.nav_profile)) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            if (session == null) {
                EmptyState(
                    title = stringResource(R.string.auth_profile_signed_out_title),
                    description = stringResource(R.string.auth_profile_signed_out_body),
                    actionLabel = stringResource(R.string.auth_sign_in_title),
                    onAction = onSignIn,
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.s8),
                )
            } else {
                ListItem(
                    modifier = Modifier.clickable(role = Role.Button, onClick = onOpenAccount),
                    leadingContent = {
                        Avatar(
                            session.user.fullName,
                            size = AvatarSize.LARGE,
                            status = AvatarStatus.ONLINE,
                            imageUrl = session.user.image,
                        )
                    },
                    headlineContent = { Text(session.user.fullName) },
                    supportingContent = { Text(session.user.email) },
                )
            }
            HorizontalDivider()
            ListItem(
                modifier = Modifier.clickable(role = Role.Button, onClick = onOpenSettings),
                headlineContent = { Text(stringResource(R.string.settings_title)) },
            )
        }
    }
}
