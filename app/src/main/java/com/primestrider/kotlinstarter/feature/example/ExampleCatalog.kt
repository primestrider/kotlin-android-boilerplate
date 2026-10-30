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

/** Home index content. Sections in order: Features, Components, SDK, Styling. */
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
        ExampleSection(
            title = R.string.example_home_components_title,
            description = R.string.example_home_components_description,
            entries =
                listOf(
                    ExampleEntry(
                        R.string.example_components_actions_title,
                        R.string.example_components_actions_subtitle,
                        ActionsRoute,
                    ),
                    ExampleEntry(
                        R.string.example_components_inputs_title,
                        R.string.example_components_inputs_subtitle,
                        InputsRoute,
                    ),
                    ExampleEntry(
                        R.string.example_components_display_title,
                        R.string.example_components_display_subtitle,
                        DisplayRoute,
                    ),
                ),
        ),
        ExampleSection(
            title = R.string.example_home_styling_title,
            description = R.string.example_home_styling_description,
            entries =
                listOf(
                    ExampleEntry(R.string.example_styling_colors_title, R.string.example_styling_colors_subtitle, ColorsRoute),
                    ExampleEntry(
                        R.string.example_styling_typography_title,
                        R.string.example_styling_typography_subtitle,
                        TypographyRoute,
                    ),
                    ExampleEntry(R.string.example_styling_spacing_title, R.string.example_styling_spacing_subtitle, SpacingRoute),
                    ExampleEntry(R.string.example_styling_sizing_title, R.string.example_styling_sizing_subtitle, SizingRoute),
                    ExampleEntry(R.string.example_styling_layout_title, R.string.example_styling_layout_subtitle, LayoutRoute),
                    ExampleEntry(
                        R.string.example_styling_appearance_title,
                        R.string.example_styling_appearance_subtitle,
                        AppearanceRoute,
                    ),
                    ExampleEntry(R.string.example_styling_theme_title, R.string.example_styling_theme_subtitle, ThemeRoute),
                ),
        ),
    )
