package com.primestrider.kotlinstarter.feature.example

import com.primestrider.kotlinstarter.app.di.ExampleCatalogEntries

/** The Home sections the running app shows: the catalog's own plus every example still contributed from `:app`. */
val exampleSections: List<ExampleSection> =
    buildSections(
        CatalogModule.catalogSections() + ExampleCatalogEntries.sections(),
        CatalogModule.catalogEntries() + ExampleCatalogEntries.entries(),
    )
