package com.primestrider.kotlinstarter.feature.example.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.theme.AppThemeTokens
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold

internal const val SUBMIT_BUTTON_TAG = "form_submit"

/** Validated form: errors come from [FormUiState.errors]; the screen only renders and reports edits. */
@Composable
fun FormExampleScreen(
    state: FormUiState,
    onChange: (FormField, String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    ExamplePageScaffold(
        title = R.string.example_form_title,
        subtitle = R.string.example_form_subtitle,
        onBack = onBack,
    ) {
        Column(Modifier.imePadding(), verticalArrangement = Arrangement.spacedBy(Spacing.s3)) {
            FormTextField(
                state,
                FormField.NAME,
                state.values.name,
                R.string.form_field_name,
                R.string.form_field_name_placeholder,
                onChange,
            )
            FormTextField(
                state,
                FormField.EMAIL,
                state.values.email,
                R.string.form_field_email,
                R.string.form_field_email_placeholder,
                onChange,
                keyboardType = KeyboardType.Email,
            )
            FormTextField(
                state,
                FormField.PHONE,
                state.values.phone,
                R.string.form_field_phone,
                R.string.form_field_phone_placeholder,
                onChange,
                keyboardType = KeyboardType.Phone,
            )
            FormTextField(
                state,
                FormField.PASSWORD,
                state.values.password,
                R.string.form_field_password,
                null,
                onChange,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.Lock else Icons.Filled.Lock,
                            contentDescription =
                                stringResource(
                                    if (passwordVisible) R.string.components_cd_hide_password else R.string.components_cd_show_password,
                                ),
                        )
                    }
                },
            )
            FormTextField(
                state,
                FormField.BIO,
                state.values.bio,
                R.string.form_field_bio,
                R.string.form_field_bio_placeholder,
                onChange,
                singleLine = false,
                counter = stringResource(R.string.components_field_counter, state.values.bio.length, BIO_MAX),
            )

            Button(
                onClick = onSubmit,
                enabled = !state.submitting,
                modifier = Modifier.fillMaxWidth().testTag(SUBMIT_BUTTON_TAG),
            ) {
                if (state.submitting) {
                    CircularProgressIndicator(Modifier.size(ButtonDefaults.IconSize), strokeWidth = Spacing.s0_5)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
                Text(stringResource(if (state.submitting) R.string.form_submitting else R.string.form_submit))
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.form_clear)) }

            state.submitted?.let { SubmittedCard(it) }
        }
    }
}

@Composable
private fun FormTextField(
    state: FormUiState,
    field: FormField,
    value: String,
    @StringRes label: Int,
    @StringRes placeholder: Int?,
    onChange: (FormField, String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    counter: String? = null,
) {
    val error = state.errors[field]
    val supporting = error?.let { stringResource(it) } ?: counter
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(field, it) },
        label = { Text(stringResource(label)) },
        placeholder = placeholder?.let { { Text(stringResource(it)) } },
        isError = error != null,
        supportingText = supporting?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SubmittedCard(values: FormValues) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Spacing.s4), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AppThemeTokens.colors.success)
                Text(stringResource(R.string.form_submitted_title), style = MaterialTheme.typography.titleSmall)
            }
            Text(stringResource(R.string.form_submitted_body), style = MaterialTheme.typography.bodyMedium)
            listOf(
                "name" to values.name,
                "email" to values.email,
                "phone" to values.phone,
                "password" to values.password,
                "bio" to values.bio,
            ).forEach { (key, value) ->
                Text(
                    "$key: $value",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
