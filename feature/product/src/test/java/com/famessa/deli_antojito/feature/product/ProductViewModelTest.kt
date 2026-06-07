package com.famessa.deli_antojito.feature.product

import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import com.famessa.deli_antojito.domain.usecase.producto.DeleteProductoUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ObserveProductosUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun emptyRepositoryShowsEmptyState() = runTest {
        val repository = FakeProductoRepository()
        val viewModel = ProductViewModel(ObserveProductosUseCase(repository), DeleteProductoUseCase(repository))

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProductListUiState.Empty)
    }

    @Test
    fun productsShowLoadedSummary() = runTest {
        val repository = FakeProductoRepository(
            listOf(
                Producto(id = 1, nombre = "Alitas", precioBase = 120.0, activo = true),
                Producto(id = 2, nombre = "Crepas", precioBase = 85.0, activo = false)
            )
        )
        val viewModel = ProductViewModel(ObserveProductosUseCase(repository), DeleteProductoUseCase(repository))

        advanceUntilIdle()

        val state = viewModel.uiState.value as ProductListUiState.Loaded
        assertEquals(2, state.summary.totalProductos)
        assertEquals(1, state.summary.totalActivos)
    }

    @Test
    fun observeFailureShowsError() = runTest {
        val repository = FakeProductoRepository(failObserve = true)
        val viewModel = ProductViewModel(ObserveProductosUseCase(repository), DeleteProductoUseCase(repository))

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProductListUiState.Error)
    }

    @Test
    fun deleteCanBeCancelled() = runTest {
        val product = Producto(id = 1, nombre = "Alitas", precioBase = 120.0)
        val repository = FakeProductoRepository(listOf(product))
        val viewModel = ProductViewModel(ObserveProductosUseCase(repository), DeleteProductoUseCase(repository))
        advanceUntilIdle()

        viewModel.requestDelete(product)
        viewModel.cancelDelete()

        val state = viewModel.uiState.value as ProductListUiState.Loaded
        assertEquals(null, state.pendingDelete)
    }

    @Test
    fun deleteFailureKeepsErrorMessage() = runTest {
        val product = Producto(id = 1, nombre = "Alitas", precioBase = 120.0)
        val repository = FakeProductoRepository(listOf(product))
        repository.deleteResult = ProductoDeleteResult.PersistenceError()
        val viewModel = ProductViewModel(ObserveProductosUseCase(repository), DeleteProductoUseCase(repository))
        advanceUntilIdle()

        viewModel.requestDelete(product)
        viewModel.confirmDelete()
        advanceUntilIdle()

        val state = viewModel.uiState.value as ProductListUiState.Loaded
        assertEquals("No se pudo eliminar el producto.", state.errorMessage)
    }
}
