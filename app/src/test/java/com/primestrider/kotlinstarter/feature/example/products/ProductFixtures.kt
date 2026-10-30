package com.primestrider.kotlinstarter.feature.example.products

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json

fun product(
    id: Long = 1,
    stock: Int = 5,
    reviews: List<ReviewDto> = emptyList(),
) = ProductDto(
    id = id,
    title = "Phone $id",
    description = "A phone.",
    category = "smartphones",
    price = 9.99,
    discountPercentage = 12.5,
    rating = 4.25,
    stock = stock,
    thumbnail = "https://example.org/$id.png",
    reviews = reviews,
)

/** A repository whose [product] answers come from [answer] and that records the queries [pager] is asked for. */
class FakeProductsRepository(
    var answer: suspend (Long) -> ProductDto = { product(it) },
) : ProductsRepository(UnusedProductsApi, Json) {
    val requested = mutableListOf<Long>()
    val queries = mutableListOf<String>()

    override fun pager(query: String): Flow<PagingData<ProductDto>> {
        queries += query
        return flowOf(PagingData.empty())
    }

    override suspend fun product(id: Long): ProductDto {
        requested += id
        return answer(id)
    }
}

object UnusedProductsApi : ProductsApi {
    override suspend fun products(
        limit: Int,
        skip: Int,
    ): ProductPage = error("unused")

    override suspend fun search(
        query: String,
        limit: Int,
        skip: Int,
    ): ProductPage = error("unused")

    override suspend fun product(id: Long): ProductDto = error("unused")
}
