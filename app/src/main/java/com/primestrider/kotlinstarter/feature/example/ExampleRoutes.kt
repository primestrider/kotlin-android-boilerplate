package com.primestrider.kotlinstarter.feature.example

import kotlinx.serialization.Serializable

/** Type-safe destinations of the example feature. */
@Serializable
data object SettingsRoute

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

@Serializable
data object ActionsRoute

@Serializable
data object InputsRoute

@Serializable
data object DisplayRoute

@Serializable
data object OverlaysRoute

@Serializable
data object FormExampleRoute

@Serializable
data object ComponentsHomeRoute

@Serializable
data object FeedbackRoute

@Serializable
data object SignInRoute

@Serializable
data object ProfileRoute

/** Protected: only reachable with a session; see the guard in `profileGraph`. */
@Serializable
data object AccountRoute

@Serializable
data object ProductsRoute

@Serializable
data class ProductDetailRoute(
    val id: Long,
)

@Serializable
data object TodosRoute

/** Every "Device APIs" demo shares this one destination; [id] picks the demo from `sdkDemos`. */
@Serializable
data class SdkDemoRoute(
    val id: String,
    /** Set only by the Payment Code deep link (`.../sdk/payment-code?amount=50000`). */
    val amount: Long? = null,
)
