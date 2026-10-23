package com.famessa.deli_antojito.feature.orders.form

import androidx.lifecycle.SavedStateHandle
import com.famessa.deli_antojito.feature.orders.*
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.*
import com.famessa.deli_antojito.domain.usecase.producto.ObserveProductosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

internal fun formModel(repo: FakePedidoRepository, saved: SavedStateHandle = SavedStateHandle(), catalog: FakeOrderCatalog = FakeOrderCatalog()) =
    OrderFormViewModel(CapturePedidoProductUseCase(catalog), CreatePedidoUseCase(repo),
        UpdatePedidoUseCase(repo), ObservePedidoUseCase(repo), ObserveProductosUseCase(catalog), saved)

@OptIn(ExperimentalCoroutinesApi::class)
class OrderCreateViewModelTest {
    @Test fun pendingCaptureCannotSaveOrLeakIntoAnotherFormEntry() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            val catalog = FakeOrderCatalog()
            val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
            catalog.waitForGet = gate
            val model = formModel(repo, catalog = catalog)
            model.start(null, "first"); model.client("Cliente"); model.add(1); runCurrent()
            assertTrue(model.uiState.value.capturing)
            model.save(); runCurrent()
            assertNull(repo.lastCreate)
            assertNull(model.uiState.value.error)
            model.start(null, "second")
            gate.complete(Unit); runCurrent()
            assertTrue(model.uiState.value.lines.isEmpty())
            assertEquals("", model.uiState.value.cliente)
            assertFalse(model.uiState.value.capturing)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun aNewNavigationEntryResetsSuccessfulFormAndToken() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            repo.writeResult = PedidoWriteResult.Success(Pedido(1,1,"Cliente",1,1,EstadoPedido.PENDIENTE,Money(1250),null,1,emptyList()))
            val handle = SavedStateHandle(); val model = formModel(repo, handle)
            model.start(null, "first"); model.client("Cliente"); model.add(1); runCurrent()
            model.save(); runCurrent()
            assertTrue(model.uiState.value.saved)
            val token = handle.get<String>("token")
            model.start(null, "second")
            assertFalse(model.uiState.value.saved)
            assertTrue(model.uiState.value.lines.isEmpty())
            assertNotEquals(token, handle.get<String>("token"))
        } finally { Dispatchers.resetMain() }
    }
    @Test fun validatesAndPreservesFormAndStableTokenOnFailureAndRestoration() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            val handle = SavedStateHandle()
            val model = formModel(repo, handle)
            model.start(null); model.save(); runCurrent()
            assertNotNull(model.uiState.value.error)
            assertNull(repo.lastCreate)
            model.client("María"); model.add(1); runCurrent()
            val key = model.uiState.value.lines.single().key
            model.quantity(key, "2"); model.add(2); runCurrent()
            model.quantity(model.uiState.value.lines.last().key, "3")
            assertEquals(4630L, model.uiState.value.total?.centavos)
            model.save(); model.save(); runCurrent()
            assertEquals(1, repo.createCalls)
            assertNotNull(model.uiState.value.error)
            assertEquals("María", model.uiState.value.cliente)
            val token = repo.lastCreate?.token
            val restored = formModel(repo, handle)
            restored.start(null); restored.save(); runCurrent()
            assertEquals(token, repo.lastCreate?.token)
            assertEquals(4630L, restored.uiState.value.total?.centavos)
            assertEquals(2, restored.uiState.value.products.size)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun quantityOverflowAndInactiveProductsNeverProduceAWrite() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository(); val model = formModel(repo)
            model.start(null); model.client("Cliente"); model.add(3); runCurrent()
            assertTrue(model.uiState.value.lines.isEmpty())
            model.add(1); runCurrent()
            model.quantity(model.uiState.value.lines.single().key, "99999999999999")
            model.save(); runCurrent()
            assertNull(model.uiState.value.total)
            assertNull(repo.lastCreate)
        } finally { Dispatchers.resetMain() }
    }
}
