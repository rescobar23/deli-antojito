package com.famessa.deli_antojito.domain.usecase.pedido

import com.famessa.deli_antojito.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CreatePedidoUseCaseTest {
    private val line = PedidoDraftLinea.Nueva(PedidoProductSnapshot(1, "Fixture", Money(1250)), 2)
    @Test fun validatesClientAndLinesBeforeWriting() = runTest {
        val repository = FakePedidoRepository()
        val useCase = CreatePedidoUseCase(repository)
        assertTrue(useCase(CrearPedidoCommand("token", PedidoDraft("  ", listOf(line)))) is PedidoWriteResult.ValidationError)
        assertTrue(useCase(CrearPedidoCommand("token", PedidoDraft("Cliente", emptyList()))) is PedidoWriteResult.ValidationError)
        assertNull(repository.lastCreate)
    }
    @Test fun calculatesExactTotalAndRejectsInvalidQuantity() {
        val lines = listOf(line, PedidoDraftLinea.Nueva(PedidoProductSnapshot(2, "Otro", Money(710)), 3))
        assertEquals(4630L, CalculatePedidoUseCase()(lines).centavos)
        assertNotNull(ValidatePedidoUseCase()(PedidoDraft("Cliente", listOf(line.copy(cantidad = 0)))))
        assertNotNull(ValidatePedidoUseCase()(PedidoDraft("Cliente", listOf(line.copy(snapshot = line.snapshot.copy(precio = Money(Long.MAX_VALUE)))))))
    }
    @Test fun preservesStableTokenAndTrimsClient() = runTest {
        val repository = FakePedidoRepository()
        CreatePedidoUseCase(repository)(CrearPedidoCommand("stable", PedidoDraft(" Cliente ", listOf(line), " ")))
        assertEquals("stable", repository.lastCreate?.token)
        assertEquals("Cliente", repository.lastCreate?.draft?.cliente)
        assertNull(repository.lastCreate?.draft?.observaciones)
    }
}
