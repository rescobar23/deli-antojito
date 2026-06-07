package com.famessa.deli_antojito.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.ProductListSummary
import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import com.famessa.deli_antojito.domain.usecase.producto.DeleteProductoUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ObserveProductosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val observeProductosUseCase: ObserveProductosUseCase,
    private val deleteProductoUseCase: DeleteProductoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        observeProductos()
    }

    fun retry() {
        observeProductos()
    }

    private fun observeProductos() {
        _uiState.value = ProductListUiState.Loading
        viewModelScope.launch {
            observeProductosUseCase()
                .catch {
                    _uiState.value = ProductListUiState.Error("No se pudieron cargar los productos.")
                }
                .collect { products ->
                    val summary = ProductListSummary(
                        totalProductos = products.size,
                        totalActivos = products.count { it.activo }
                    )
                    _uiState.value = if (products.isEmpty()) {
                        ProductListUiState.Empty(summary)
                    } else {
                        ProductListUiState.Loaded(products, summary)
                    }
                }
        }
    }

    fun requestDelete(producto: Producto) {
        val state = _uiState.value
        if (state is ProductListUiState.Loaded) {
            _uiState.value = state.copy(pendingDelete = producto, errorMessage = null)
        }
    }

    fun cancelDelete() {
        val state = _uiState.value
        if (state is ProductListUiState.Loaded) {
            _uiState.value = state.copy(pendingDelete = null)
        }
    }

    fun confirmDelete() {
        val state = _uiState.value as? ProductListUiState.Loaded ?: return
        val pending = state.pendingDelete ?: return
        viewModelScope.launch {
            when (deleteProductoUseCase(pending.id)) {
                ProductoDeleteResult.Success -> _uiState.value = state.copy(pendingDelete = null)
                ProductoDeleteResult.ProductNotFound -> _uiState.value = state.copy(
                    pendingDelete = null,
                    errorMessage = "El producto ya no existe."
                )
                is ProductoDeleteResult.PersistenceError -> _uiState.value = state.copy(
                    pendingDelete = null,
                    errorMessage = "No se pudo eliminar el producto."
                )
            }
        }
    }
}
