package com.primestrider.kotlinstarter.feature.example.products.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.primestrider.kotlinstarter.feature.example.products.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** Product data for the example screens, as [Product] models. */
interface ProductsRepository {
    /** Pages of products matching [query] (all products when blank). */
    fun pager(query: String): Flow<PagingData<Product>>

    suspend fun product(id: Long): Product
}

/** [ProductsRepository] on DummyJSON: pages through [ProductsPagingSource] and maps every DTO to [Product]. */
class DefaultProductsRepository
    @Inject
    constructor(
        private val api: ProductsApi,
        private val json: Json,
    ) : ProductsRepository {
        override fun pager(query: String): Flow<PagingData<Product>> =
            Pager(PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE, enablePlaceholders = false)) {
                ProductsPagingSource(api, query, json)
            }.flow.map { page -> page.map { it.toProduct() } }

        override suspend fun product(id: Long): Product = api.product(id).toProduct()
    }
