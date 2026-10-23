package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdatePedidoUseCaseTest {
    @Test fun preservesHistoricalReferenceAndRevisionWhileNormalizingText() = runTest {
        val repo = FakePedidoRepository()
        val command = ActualizarPedidoCommand(1, 7, PedidoDraft(" Cliente ", listOf(PedidoDraftLinea.Existente(9, 3)), " Nota "))
        UpdatePedidoUseCase(repo)(command)
        assertEquals(command.copy(draft = command.draft.copy(cliente = "Cliente", observaciones = "Nota")), repo.lastUpdate)
    }
    @Test fun invalidContentNeverReachesPersistence() = runTest {
        val repo = FakePedidoRepository()
        val result = UpdatePedidoUseCase(repo)(ActualizarPedidoCommand(1, 7, PedidoDraft("", emptyList())))
        assertTrue(result is PedidoWriteResult.ValidationError)
        assertNull(repo.lastUpdate)
    }
}

