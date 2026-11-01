package com.primestrider.kotlinstarter.feature.example

import kotlinx.serialization.Serializable

/** The four pages of the "Animations" Home section; see `animationPages`. */
@Serializable
data object AnimMicroRoute

@Serializable
data object AnimCardsRoute

@Serializable
data object AnimListsRoute

@Serializable
data object AnimTransitionsRoute
