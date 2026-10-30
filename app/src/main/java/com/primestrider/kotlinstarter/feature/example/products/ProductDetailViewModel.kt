package com.primestrider.kotlinstarter.feature.example.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.Messenger
import com.primestrider.kotlinstarter.core.designsystem.component.UiText
import com.primestrider.kotlinstarter.core.network.ApiError
import com.primestrider.kotlinstarter.core.network.toApiError
import com.primestrider.kotlinstarter.feature.example.ProductDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val HTTP_NOT_FOUND = 404

sealed interface ProductDetailState {
    data object Loading : ProductDetailState

    data class Loaded(
        val product: ProductDto,
    ) : ProductDetailState

    /** The id is invalid or the server answered 404: nothing to retry. */
    data object NotFound : ProductDetailState

    data class Failed(
        val error: ApiError,
    ) : ProductDetailState
}

/** Loads one product by the id in [ProductDetailRoute]; "add to cart" only confirms (the example has no cart). */
@HiltViewModel
class ProductDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val repository: ProductsRepository,
        private val json: Json,
        private val messenger: Messenger,
    ) : ViewModel() {
        private val id = savedStateHandle.toRoute<ProductDetailRoute>().id
        private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Loading)
        val state: StateFlow<ProductDetailState> = _state.asStateFlow()
        private var loading: Job? = null

        init {
            load()
        }

        fun retry() = load()

        fun addToCart(
            @Suppress("UNUSED_PARAMETER") quantity: Int,
        ) {
            if (_state.value is ProductDetailState.Loaded) messenger.success(UiText.Resource(R.string.products_added_to_cart))
        }

        private fun load() {
            if (id <= 0) {
                _state.value = ProductDetailState.NotFound
                return
            }
            loading?.cancel()
            _state.value = ProductDetailState.Loading
            loading =
                viewModelScope.launch {
                    _state.value =
                        try {
                            ProductDetailState.Loaded(repository.product(id))
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            val error = e.toApiError(json)
                            if (error.status == HTTP_NOT_FOUND) ProductDetailState.NotFound else ProductDetailState.Failed(error)
                        }
                }
        }
    }
