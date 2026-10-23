package com.famessa.deli_antojito.feature.orders.list

import com.famessa.deli_antojito.feature.orders.FakePedidoRepository
import com.famessa.deli_antojito.domain.model.PedidoReadResult
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.ObservePedidosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {
    @Test fun loadingBecomesPopulatedSuccessWithCardIdentity() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            repo.list.value = PedidoReadResult.Success(listOf(PedidoResumen(9, 25, "Cliente", 1, EstadoPedido.PENDIENTE, Money(100))))
            val model = OrdersViewModel(ObservePedidosUseCase(repo))
            assertTrue(model.uiState.value.loading)
            runCurrent()
            assertFalse(model.uiState.value.loading)
            assertEquals(25L, model.uiState.value.pedidos.single().numero)
            assertNull(model.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun distinguishesFailureFromEmptyAndCanRetry() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakePedidoRepository()
            val viewModel = OrdersViewModel(ObservePedidosUseCase(repository))
            runCurrent()
            assertFalse(viewModel.uiState.value.loading)
            assertNull(viewModel.uiState.value.error)
            repository.list.value = PedidoReadResult.PersistenceError
            runCurrent()
            assertNotNull(viewModel.uiState.value.error)
            repository.list.value = PedidoReadResult.Success(emptyList())
            viewModel.retry()
            runCurrent()
            assertNull(viewModel.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }
}
