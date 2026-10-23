package com.famessa.deli_antojito.feature.orders.list

import androidx.lifecycle.SavedStateHandle
import com.famessa.deli_antojito.feature.orders.FakePedidoRepository
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.ObservePedidosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersFiltersViewModelTest {
    @Test fun searchDebouncesButTabUpdatesImmediately() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            val model = OrdersViewModel(ObservePedidosUseCase(repo))
            runCurrent()
            model.search("María"); runCurrent()
            assertEquals("", repo.queries.last().texto)
            advanceTimeBy(299); runCurrent()
            assertEquals("", repo.queries.last().texto)
            advanceTimeBy(1); runCurrent()
            assertEquals("María", repo.queries.last().texto)
            model.selectTab(EstadoPedido.CANCELADO); runCurrent()
            assertEquals(EstadoPedido.CANCELADO, repo.queries.last().estadoPestana)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun restoresCriteriaWithoutLosingSearchWhenFiltersChange() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val saved = SavedStateHandle()
            val model = OrdersViewModel(ObservePedidosUseCase(FakePedidoRepository()), saved)
            model.search("María"); model.selectTab(EstadoPedido.PENDIENTE)
            model.applyFilters(PedidoConsulta(minimo = Money(100)))
            advanceTimeBy(300); runCurrent()
            assertEquals("María", model.uiState.value.query.texto)
            assertEquals(EstadoPedido.PENDIENTE, model.uiState.value.query.estadoPestana)
            val restored = OrdersViewModel(ObservePedidosUseCase(FakePedidoRepository()), saved)
            assertEquals(model.uiState.value.query, restored.uiState.value.query)
        } finally { Dispatchers.resetMain() }
    }
}
