package com.famessa.deli_antojito.domain.usecase.pedido

import com.famessa.deli_antojito.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class ValidatePedidoConsultaUseCaseTest {
    @Test fun normalizesUnicodeAndKeepsAccentsAndLiteralWildcards() {
        assertEquals(normalizePedidoSearch(" María "), normalizePedidoSearch("MARI\u0301A"))
        assertEquals("%_", normalizePedidoSearch("%_"))
        assertEquals("00025", normalizePedidoSearch(" #00025 "))
        assertNotEquals(normalizePedidoSearch("María"), normalizePedidoSearch("Maria"))
    }
    @Test fun distinguishesUnrestrictedAndEmptyIntersection() {
        assertNull(effectivePedidoStates(PedidoConsulta()))
        assertEquals(emptySet<EstadoPedido>(), effectivePedidoStates(PedidoConsulta(
            estadoPestana = EstadoPedido.PENDIENTE, estadosAvanzados = setOf(EstadoPedido.CANCELADO))))
    }
    @Test fun parsesExactAmountsWithoutRoundingOrNegativeValues() {
        assertEquals(1234L, parsePedidoAmount("12.34").centavos)
        assertEquals(1234L, parsePedidoAmount("12,34").centavos)
        assertEquals(0L, parsePedidoAmount("0").centavos)
        for (text in listOf("-1", "1.001", "1,000.00", "NaN", "1e2")) {
            assertThrows(IllegalArgumentException::class.java) { parsePedidoAmount(text) }
        }
        assertFalse(ValidatePedidoConsultaUseCase()(PedidoConsulta(minimo = Money(2), maximo = Money(1))))
    }
}
