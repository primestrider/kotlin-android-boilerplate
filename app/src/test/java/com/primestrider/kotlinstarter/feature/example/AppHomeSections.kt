package com.primestrider.kotlinstarter.feature.example

import com.primestrider.kotlinstarter.app.di.ExampleCatalogEntries
import com.primestrider.kotlinstarter.feature.example.products.data.ProductsModule
import com.primestrider.kotlinstarter.feature.example.sdk.DevicesCatalogModule
import com.primestrider.kotlinstarter.feature.example.todos.data.TodosModule

/** The Home sections the running app shows: what the catalog, the feature modules and :app contribute. */
val exampleSections: List<ExampleSection> =
    buildSections(
        CatalogModule.catalogSections() + ExampleCatalogEntries.sections() + DevicesCatalogModule.section(),
        CatalogModule.catalogEntries() + ExampleCatalogEntries.entries() + ProductsModule.homeEntry() + TodosModule.homeEntry() +
            DevicesCatalogModule.entries(),
    )
