package com.primestrider.kotlinstarter.feature.example.sdk.blob

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.AppButton
import com.primestrider.kotlinstarter.core.designsystem.component.AppTextField
import com.primestrider.kotlinstarter.core.designsystem.component.ButtonVariant
import com.primestrider.kotlinstarter.core.designsystem.component.InfoCard
import com.primestrider.kotlinstarter.core.designsystem.theme.Spacing
import com.primestrider.kotlinstarter.feature.example.ExamplePageScaffold
import com.primestrider.kotlinstarter.feature.example.sdk.SdkSection

private const val INITIAL_CONTENT = """{"trx":"TRX-001","amount":50000,"status":"PAID"}"""
private const val JSON_TYPE = "application/json"
private const val TEXT_TYPE = "text/plain"
private const val SLICE_END = 8

private enum class Detail { NONE, TEXT, BYTES }

private val payloadSaver =
    Saver<BinaryPayload?, List<Any>>(
        save = { it?.let { payload -> listOf(payload.bytes, payload.type) } },
        restore = { BinaryPayload(it[0] as ByteArray, it[1] as String) },
    )

/** Build a [BinaryPayload] from text, then read it back as text, slice its first bytes or combine it with itself. */
@Composable
fun BlobScreen(onBack: () -> Unit) {
    var content by rememberSaveable { mutableStateOf(INITIAL_CONTENT) }
    var payload by rememberSaveable(stateSaver = payloadSaver) { mutableStateOf<BinaryPayload?>(null) }
    var detail by rememberSaveable { mutableStateOf(Detail.NONE) }
    var hex by rememberSaveable { mutableStateOf("") }

    ExamplePageScaffold(title = R.string.sdk_blob_title, subtitle = R.string.sdk_blob_subtitle, onBack = onBack) {
        SdkSection(R.string.sdk_blob_section_title, R.string.sdk_blob_section_description) {
            AppTextField(value = content, onValueChange = { content = it }, label = stringResource(R.string.sdk_blob_content))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                AppButton(
                    stringResource(R.string.sdk_blob_create),
                    onClick = {
                        payload = BinaryPayload.of(content, JSON_TYPE)
                        detail = Detail.NONE
                    },
                )
                AppButton(
                    stringResource(R.string.sdk_blob_text),
                    onClick = { detail = Detail.TEXT },
                    variant = ButtonVariant.OUTLINED,
                    enabled = payload != null,
                )
                AppButton(
                    stringResource(R.string.sdk_blob_slice),
                    onClick = {
                        hex = payload?.slice(0, SLICE_END)?.hex().orEmpty()
                        detail = Detail.BYTES
                    },
                    variant = ButtonVariant.OUTLINED,
                    enabled = payload != null,
                )
                AppButton(
                    stringResource(R.string.sdk_blob_combine),
                    onClick = {
                        payload?.let { payload = BinaryPayload.combine(listOf(it, "\n", it), TEXT_TYPE) }
                        detail = Detail.TEXT
                    },
                    variant = ButtonVariant.OUTLINED,
                    enabled = payload != null,
                )
            }
            payload?.let { current ->
                val rows =
                    buildList {
                        add(stringResource(R.string.sdk_blob_size) to stringResource(R.string.sdk_blob_size_value, current.size))
                        add(stringResource(R.string.sdk_blob_type) to current.type)
                        when (detail) {
                            Detail.TEXT -> add(stringResource(R.string.sdk_blob_result_text) to current.text())
                            Detail.BYTES -> add(stringResource(R.string.sdk_blob_result_bytes) to hex)
                            Detail.NONE -> Unit
                        }
                    }
                InfoCard(rows)
            }
        }
    }
}
