package com.primestrider.kotlinstarter.feature.example.products

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private class FakeSource : ProductsRepository(UnusedApi, Json) {
        val queries = mutableListOf<String>()

        override fun pager(query: String): Flow<PagingData<ProductDto>> {
            queries += query
            return flowOf(PagingData.empty())
        }
    }

    private object UnusedApi : ProductsApi {
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

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun first_page_loads_immediately_then_typing_is_debounced() =
        runTest(dispatcher) {
            val source = FakeSource()
            val viewModel = ProductsViewModel(source, SavedStateHandle())
            backgroundScope.launch { viewModel.products.collect {} }
            runCurrent()
            assertEquals(listOf(""), source.queries)

            viewModel.onQueryChange("p")
            advanceTimeBy(100)
            viewModel.onQueryChange("ph")
            advanceTimeBy(100)
            viewModel.onQueryChange("pho")
            advanceTimeBy(349)
            runCurrent()
            assertEquals(listOf(""), source.queries)
            advanceTimeBy(2)
            runCurrent()
            assertEquals(listOf("", "pho"), source.queries)
        }

    @Test
    fun whitespace_only_query_does_not_reload() =
        runTest(dispatcher) {
            val source = FakeSource()
            val viewModel = ProductsViewModel(source, SavedStateHandle())
            backgroundScope.launch { viewModel.products.collect {} }
            runCurrent()
            viewModel.onQueryChange("   ")
            advanceTimeBy(1_000)
            runCurrent()
            assertEquals(listOf(""), source.queries)
            assertEquals("   ", viewModel.query.value)
        }

    @Test
    fun query_survives_recreation_from_saved_state() =
        runTest(dispatcher) {
            val handle = SavedStateHandle()
            ProductsViewModel(FakeSource(), handle).onQueryChange("laptop")
            val source = FakeSource()
            val restored = ProductsViewModel(source, handle)
            backgroundScope.launch { restored.products.collect {} }
            runCurrent()
            assertEquals("laptop", restored.query.value)
            assertEquals(listOf("laptop"), source.queries)
        }
}
