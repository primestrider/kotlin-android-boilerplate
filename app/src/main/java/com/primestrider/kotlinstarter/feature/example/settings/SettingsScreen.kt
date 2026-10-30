package com.primestrider.kotlinstarter.feature.example.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.i18n.AppLanguage
import com.primestrider.kotlinstarter.feature.example.ThemeModeSelector

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.s4),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_action_back),
                )
            }
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
        }
        Text(stringResource(R.string.settings_theme_label), style = MaterialTheme.typography.titleMedium)
        ThemeModeSelector(selected = state.themeMode, onSelect = onThemeModeChange)
        Text(stringResource(R.string.settings_language_label), style = MaterialTheme.typography.titleMedium)
        Column(Modifier.selectableGroup()) {
            AppLanguage.entries.forEach { language ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = state.language == language,
                            onClick = { onLanguageChange(language) },
                            role = Role.RadioButton,
                        ).padding(vertical = Spacing.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = state.language == language, onClick = null)
                    Text(language.nativeName, Modifier.padding(start = Spacing.s3))
                }
            }
        }
    }
}
