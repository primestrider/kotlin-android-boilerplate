package com.primestrider.kotlinstarter.feature.example

import kotlinx.serialization.Serializable

// Routes of the catalog feature. They keep the `feature.example` package: the serial name (the package-qualified
// class name) is what the saved back stack stores, so moving modules must not change it.

/** Root of the Components tab. */
@Serializable
data object ComponentsHomeRoute

@Serializable
data object ActionsRoute

@Serializable
data object InputsRoute

@Serializable
data object DisplayRoute

@Serializable
data object OverlaysRoute

@Serializable
data object FeedbackRoute

@Serializable
data object FormExampleRoute

@Serializable
data object ColorsRoute

@Serializable
data object TypographyRoute

@Serializable
data object SpacingRoute

@Serializable
data object SizingRoute

@Serializable
data object LayoutRoute

@Serializable
data object AppearanceRoute

@Serializable
data object ThemeRoute
