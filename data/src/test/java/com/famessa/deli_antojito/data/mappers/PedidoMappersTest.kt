package com.famessa.deli_antojito.data.mappers

import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PedidoMappersTest {
    private fun header(state: String = "PENDIENTE") = Pedido(1, 25, "token", "Cliente", "cliente", "00025", 10, 10, state, 1250, null, 1)
    private fun line() = PedidoDetalle(1, 1, 99, "Producto histórico", 1, 1250, 1250, 0)

    @Test fun mapsHistoricalProductWithoutCatalogLookup() {
        val order = header().toDomain(listOf(line()))
        assertEquals("Producto histórico", order.lineas.single().nombreProducto)
        assertEquals(1250L, order.total.centavos)
    }
    @Test fun rejectsUnknownStateAndCorruptTotals() {
        assertThrows(IllegalArgumentException::class.java) { header("UNKNOWN").toDomain(listOf(line())) }
        assertThrows(IllegalArgumentException::class.java) { header().copy(totalCentavos = 1).toDomain(listOf(line())) }
        assertThrows(IllegalArgumentException::class.java) { header().toDomain(emptyList()) }
    }
}
