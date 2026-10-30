package com.primestrider.kotlinstarter.feature.example.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.DialogProperties
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.DemoSection
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import kotlinx.coroutines.launch

/** Material 3 dialogs, bottom sheet, snackbars, menus and tooltips. */
@Composable
fun OverlaysScreen(onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    ExamplePageScaffold(
        title = R.string.example_components_overlays_title,
        subtitle = R.string.example_components_overlays_subtitle,
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        DialogsSection()
        BottomSheetSection()
        SnackbarSection(snackbarHostState)
        MenuSection()
        TooltipSection()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DialogsSection() {
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showBlocking by rememberSaveable { mutableStateOf(false) }
    DemoSection(R.string.components_section_dialogs) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s2),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            OutlinedButton(onClick = { showDelete = true }) { Text(stringResource(R.string.components_dialog_open)) }
            OutlinedButton(onClick = { showBlocking = true }) { Text(stringResource(R.string.components_dialog_open_blocking)) }
        }
    }
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text(stringResource(R.string.components_dialog_delete_title)) },
            text = { Text(stringResource(R.string.components_dialog_delete_text)) },
            confirmButton = {
                TextButton(onClick = { showDelete = false }) {
                    Text(stringResource(R.string.components_dialog_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(stringResource(R.string.components_dialog_cancel)) }
            },
        )
    }
    if (showBlocking) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.components_dialog_blocking_title)) },
            text = { Text(stringResource(R.string.components_dialog_blocking_text)) },
            confirmButton = {
                Button(onClick = { showBlocking = false }) { Text(stringResource(R.string.components_dialog_accept)) }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetSection() {
    var open by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    DemoSection(R.string.components_section_bottom_sheet) {
        OutlinedButton(onClick = { open = true }) { Text(stringResource(R.string.components_sheet_open)) }
        Text(stringResource(R.string.components_sheet_sorted_by, stringResource(sortOptions[selected])))
    }
    if (open) {
        ModalBottomSheet(onDismissRequest = { open = false }, sheetState = sheetState) {
            Text(
                stringResource(R.string.components_select_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = Spacing.s4, vertical = Spacing.s2),
            )
            Column(Modifier.selectableGroup().padding(bottom = Spacing.s6)) {
                sortOptions.forEachIndexed { index, option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(selected = selected == index, role = Role.RadioButton, onClick = {
                                selected = index
                                scope.launch { sheetState.hide() }.invokeOnCompletion { open = false }
                            })
                            .padding(horizontal = Spacing.s4, vertical = Spacing.s3),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s3),
                    ) {
                        RadioButton(selected = selected == index, onClick = null)
                        Text(stringResource(option))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SnackbarSection(hostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    val resources = LocalResources.current
    val messages =
        listOf(
            R.string.components_snackbar_success to R.string.components_snackbar_success_message,
            R.string.components_snackbar_error to R.string.components_snackbar_error_message,
            R.string.components_snackbar_warning to R.string.components_snackbar_warning_message,
            R.string.components_snackbar_info to R.string.components_snackbar_info_message,
        )
    DemoSection(R.string.components_section_snackbar) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s2),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            messages.forEach { (label, message) ->
                OutlinedButton(onClick = {
                    scope.launch {
                        hostState.currentSnackbarData?.dismiss()
                        hostState.showSnackbar(resources.getString(message), duration = SnackbarDuration.Short)
                    }
                }) { Text(stringResource(label)) }
            }
        }
        Button(onClick = {
            scope.launch {
                hostState.currentSnackbarData?.dismiss()
                val result =
                    hostState.showSnackbar(
                        message = resources.getString(R.string.components_snackbar_deleted),
                        actionLabel = resources.getString(R.string.components_snackbar_undo),
                        duration = SnackbarDuration.Short,
                    )
                if (result == SnackbarResult.ActionPerformed) {
                    hostState.showSnackbar(resources.getString(R.string.components_snackbar_restored), duration = SnackbarDuration.Short)
                }
            }
        }) { Text(stringResource(R.string.components_snackbar_delete_undo)) }
    }
}

@Composable
private fun MenuSection() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var lastAction by rememberSaveable { mutableStateOf<Int?>(null) }
    DemoSection(R.string.components_section_menus) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.components_menu_open))
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf(
                        R.string.components_menu_share to Icons.Filled.Share,
                        R.string.components_menu_rename to Icons.Filled.Edit,
                    ).forEach { (label, icon) ->
                        DropdownMenuItem(
                            text = { Text(stringResource(label)) },
                            leadingIcon = { Icon(icon, contentDescription = null) },
                            onClick = {
                                lastAction = label
                                expanded = false
                            },
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.components_menu_delete), color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            lastAction = R.string.components_menu_delete
                            expanded = false
                        },
                    )
                }
            }
            lastAction?.let { Text(stringResource(R.string.components_menu_last_action, stringResource(it))) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipSection() {
    val scope = rememberCoroutineScope()
    val richState = rememberTooltipState(isPersistent = true)
    DemoSection(R.string.components_section_tooltips) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text(stringResource(R.string.components_tooltip_plain)) } },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = stringResource(R.string.components_tooltip_plain))
                }
            }
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = {
                    RichTooltip(
                        title = { Text(stringResource(R.string.components_tooltip_rich_title)) },
                        action = {
                            TextButton(onClick = { richState.dismiss() }) {
                                Text(stringResource(R.string.components_tooltip_rich_action))
                            }
                        },
                    ) { Text(stringResource(R.string.components_tooltip_rich_text)) }
                },
                state = richState,
            ) {
                TextButton(onClick = { scope.launch { richState.show() } }) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Text(stringResource(R.string.components_tooltip_rich_anchor), Modifier.padding(start = Spacing.s2))
                }
            }
        }
    }
}
