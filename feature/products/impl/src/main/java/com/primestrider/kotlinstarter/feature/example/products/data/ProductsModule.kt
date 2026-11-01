package com.primestrider.kotlinstarter.feature.example.products.data

import com.primestrider.kotlinstarter.feature.catalog.api.CatalogEntry
import com.primestrider.kotlinstarter.feature.catalog.api.CatalogSections
import com.primestrider.kotlinstarter.feature.example.ProductsRoute
import com.primestrider.kotlinstarter.feature.products.impl.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsModule {
    @Binds
    abstract fun bindRepository(impl: DefaultProductsRepository): ProductsRepository

    companion object {
        @Provides
        @Singleton
        fun productsApi(retrofit: Retrofit): ProductsApi = retrofit.create(ProductsApi::class.java)

        /** The Products row on Home. */
        @Provides
        @IntoSet
        fun homeEntry(): CatalogEntry =
            CatalogEntry(R.string.products_title, R.string.products_subtitle, ProductsRoute, CatalogSections.FEATURES, order = 10)
    }
}
