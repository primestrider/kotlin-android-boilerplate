package com.primestrider.kotlinstarter.feature.example

import kotlinx.serialization.Serializable

/** Type-safe destinations of the example feature. */
@Serializable
data object SignInRoute

@Serializable
data object ProfileRoute

/** Protected: only reachable with a session; see the guard in `profileGraph`. */
@Serializable
data object AccountRoute

/** Every "Device APIs" demo shares this one destination; [id] picks the demo from `sdkDemos`. */
@Serializable
data class SdkDemoRoute(
    val id: String,
    /** Set only by the Payment Code deep link (`.../sdk/payment-code?amount=50000`). */
    val amount: Long? = null,
)

/** The four pages of the "Animations" Home section; see `animationPages`. */
@Serializable
data object AnimMicroRoute

@Serializable
data object AnimCardsRoute

@Serializable
data object AnimListsRoute

@Serializable
data object AnimTransitionsRoute
