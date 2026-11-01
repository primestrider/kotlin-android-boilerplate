package com.primestrider.kotlinstarter.app.di

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.feature.catalog.api.CatalogEntry
import com.primestrider.kotlinstarter.feature.catalog.api.CatalogSection
import com.primestrider.kotlinstarter.feature.catalog.api.CatalogSections
import com.primestrider.kotlinstarter.feature.example.TodosRoute
import com.primestrider.kotlinstarter.feature.example.animation.animationEntries
import com.primestrider.kotlinstarter.feature.example.sdk.sdkEntries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

/** Home rows of the examples still living in `:app`; each moves into its feature module with that feature. */
@Module
@InstallIn(SingletonComponent::class)
object ExampleCatalogEntries {
    @Provides
    @ElementsIntoSet
    fun sections(): Set<CatalogSection> =
        setOf(
            CatalogSection(
                CatalogSections.DEVICES,
                R.string.example_home_sdk_title,
                R.string.example_home_sdk_description,
                Icons.Filled.Phone,
                order = 20,
            ),
            CatalogSection(
                CatalogSections.ANIMATION,
                R.string.example_home_anim_title,
                R.string.example_home_anim_description,
                Icons.Filled.PlayArrow,
                order = 30,
            ),
        )

    @Provides
    @ElementsIntoSet
    fun entries(): Set<CatalogEntry> =
        setOf(
            CatalogEntry(R.string.todos_title, R.string.todos_subtitle, TodosRoute, CatalogSections.FEATURES, 20),
        ) + sdkEntries() + animationEntries()
}
