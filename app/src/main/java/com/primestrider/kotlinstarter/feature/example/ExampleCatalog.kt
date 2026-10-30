package com.primestrider.kotlinstarter.feature.example

import androidx.annotation.StringRes
import com.primestrider.kotlinstarter.R

data class ExampleEntry(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val route: Any,
)

data class ExampleSection(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val entries: List<ExampleEntry>,
)

/** Home index content. Append sections in order: Features, Components, SDK, Styling. */
val exampleSections: List<ExampleSection> =
    listOf(
        ExampleSection(
            title = R.string.example_home_features_title,
            description = R.string.example_home_features_description,
            entries =
                listOf(
                    ExampleEntry(
                        title = R.string.settings_title,
                        subtitle = R.string.example_home_settings_subtitle,
                        route = SettingsRoute,
                    ),
                ),
        ),
    )
