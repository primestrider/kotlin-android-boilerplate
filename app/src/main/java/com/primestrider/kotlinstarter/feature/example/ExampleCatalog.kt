package com.primestrider.kotlinstarter.feature.example

import androidx.annotation.StringRes
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.feature.example.animation.animationEntries
import com.primestrider.kotlinstarter.feature.example.sdk.sdkEntries

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

/** Root of the Components tab. */
val componentEntries: List<ExampleEntry> =
    listOf(
        ExampleEntry(R.string.example_components_actions_title, R.string.example_components_actions_subtitle, ActionsRoute),
        ExampleEntry(R.string.example_components_inputs_title, R.string.example_components_inputs_subtitle, InputsRoute),
        ExampleEntry(R.string.example_components_display_title, R.string.example_components_display_subtitle, DisplayRoute),
        ExampleEntry(R.string.example_components_overlays_title, R.string.example_components_overlays_subtitle, OverlaysRoute),
        ExampleEntry(R.string.example_components_feedback_title, R.string.example_components_feedback_subtitle, FeedbackRoute),
    )

/** Home tab content: Features, Device APIs, Animations, then Styling. Components and Settings have their own tabs. */
val exampleSections: List<ExampleSection> =
    listOf(
        ExampleSection(
            title = R.string.example_home_features_title,
            description = R.string.example_home_features_description,
            entries =
                listOf(
                    ExampleEntry(R.string.products_title, R.string.products_subtitle, ProductsRoute),
                    ExampleEntry(R.string.todos_title, R.string.todos_subtitle, TodosRoute),
                    ExampleEntry(R.string.example_form_title, R.string.example_form_subtitle, FormExampleRoute),
                ),
        ),
        ExampleSection(
            title = R.string.example_home_sdk_title,
            description = R.string.example_home_sdk_description,
            entries = sdkEntries(),
        ),
        ExampleSection(
            title = R.string.example_home_anim_title,
            description = R.string.example_home_anim_description,
            entries = animationEntries(),
        ),
        ExampleSection(
            title = R.string.example_home_styling_title,
            description = R.string.example_home_styling_description,
            entries =
                listOf(
                    ExampleEntry(R.string.example_styling_colors_title, R.string.example_styling_colors_subtitle, ColorsRoute),
                    ExampleEntry(R.string.example_styling_typography_title, R.string.example_styling_typography_subtitle, TypographyRoute),
                    ExampleEntry(R.string.example_styling_spacing_title, R.string.example_styling_spacing_subtitle, SpacingRoute),
                    ExampleEntry(R.string.example_styling_sizing_title, R.string.example_styling_sizing_subtitle, SizingRoute),
                    ExampleEntry(R.string.example_styling_layout_title, R.string.example_styling_layout_subtitle, LayoutRoute),
                    ExampleEntry(R.string.example_styling_appearance_title, R.string.example_styling_appearance_subtitle, AppearanceRoute),
                    ExampleEntry(R.string.example_styling_theme_title, R.string.example_styling_theme_subtitle, ThemeRoute),
                ),
        ),
    )
