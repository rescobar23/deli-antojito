package com.famessa.deli_antojito.feature.orders.form

import com.famessa.deli_antojito.feature.orders.FakePedidoRepository
import com.famessa.deli_antojito.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderEditViewModelTest {
    @Test fun delayedOldOrderLoadCannotReplaceANewFormEntry() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            repo.detail.value = PedidoReadResult.Success(Pedido(5,25,"Pedido anterior",1,1,EstadoPedido.PENDIENTE,Money(100),null,1,
                listOf(PedidoLinea(9,1,"Histórico",1,Money(100),Money(100),0))))
            val model = formModel(repo)
            model.start(5, "old")
            model.start(null, "new")
            runCurrent()
            assertEquals("", model.uiState.value.cliente)
            assertTrue(model.uiState.value.lines.isEmpty())
            assertEquals("new", model.uiState.value.entryKey)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun historicalLinesUseExistingIdsAndConflictKeepsDraft() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            repo.detail.value = PedidoReadResult.Success(Pedido(5, 25, "Cliente", 1, 1, EstadoPedido.PENDIENTE, Money(100), null, 3,
                listOf(PedidoLinea(9, 999, "Producto borrado", 1, Money(100), Money(100), 0))))
            repo.writeResult = PedidoWriteResult.Conflict
            val model = formModel(repo); model.start(5); runCurrent()
            model.quantity("9", "3"); model.save(); runCurrent()
            assertEquals(300L, model.uiState.value.total?.centavos)
            assertEquals(PedidoDraftLinea.Existente(9, 3), repo.lastUpdate?.draft?.lineas?.single())
            assertEquals(3L, repo.lastUpdate?.expectedRevision)
            assertTrue(model.uiState.value.dirty)
            assertNotNull(model.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }
}
