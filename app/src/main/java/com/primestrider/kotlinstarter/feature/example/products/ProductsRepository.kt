package com.primestrider.kotlinstarter.feature.example.products

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** Product data for the example screens. Open so ViewModel tests can record calls without a server. */
open class ProductsRepository
    @Inject
    constructor(
        private val api: ProductsApi,
        private val json: Json,
    ) {
        open fun pager(query: String): Flow<PagingData<ProductDto>> =
            Pager(PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE, enablePlaceholders = false)) {
                ProductsPagingSource(api, query, json)
            }.flow

        open suspend fun product(id: Long): ProductDto = api.product(id)
    }
