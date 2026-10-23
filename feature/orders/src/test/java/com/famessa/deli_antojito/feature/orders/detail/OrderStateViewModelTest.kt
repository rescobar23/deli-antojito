package com.famessa.deli_antojito.feature.orders.detail

import com.famessa.deli_antojito.feature.orders.FakePedidoRepository
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderStateViewModelTest {
    @Test fun sendsExpectedRevisionAndSurfacesConflictsWithoutDiscardingDetail() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            repo.detail.value = PedidoReadResult.Success(Pedido(1,1,"Cliente",1,1,EstadoPedido.PENDIENTE,Money(100),null,4,emptyList()))
            repo.writeResult = PedidoWriteResult.Conflict
            val model = OrderDetailViewModel(ObservePedidoUseCase(repo), ChangePedidoStateUseCase(repo))
            model.start(1); runCurrent()
            model.transition(EstadoPedido.CANCELADO); model.transition(EstadoPedido.COMPLETADO); runCurrent()
            assertEquals(CambiarEstadoPedidoCommand(1,4,EstadoPedido.CANCELADO), repo.lastTransition)
            assertNotNull(model.uiState.value.error)
            assertEquals(1L, model.uiState.value.pedido?.id)
            model.retry(); runCurrent()
            assertNull(model.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }
}
