package com.famessa.deli_antojito.domain.usecase.cierre
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*
import org.junit.Test
import org.junit.Assert.*
class CalculateConsultaCierreTopProductsTest {
    private fun rank(orders: List<PedidoConsultaSnapshot>) = (CalculateConsultaCierreUseCase()(period,orders) as ConsultaCierreResult.Success).consulta.topProductos
    @Test fun groupsByProductWithoutMultiplyingHeaders() { val products=rank(fixture()); assertEquals(listOf(7L,8L),products.map { it.productoId }); assertEquals(4,products.first().cantidad); assertEquals(400,products.first().bruto.centavos) }
    @Test fun topFiveTieIsStableById() {
        val orders=(1L..7L).reversed().map { id -> PedidoConsultaSnapshot(id,1000,Money(100),listOf(line(id,id,1))) }
        assertEquals((1L..5L).toList(),rank(orders).map { it.productoId })
    }
    @Test fun latestHistoricalNameWins() {
        val orders=fixture().mapIndexed { index,order -> order.copy(lineas=order.lineas.map { it.copy(nombreHistorico=if(index==1) "Nuevo histórico" else "Antiguo") }) }
        assertEquals("Nuevo histórico",rank(orders).first().nombreHistorico)
    }
    @Test fun grossBreaksQuantityTie() {
        val orders=listOf(PedidoConsultaSnapshot(1,1000,Money(300),listOf(line(1,7,1),line(2,8,1,200))))
        assertEquals(8,rank(orders).first().productoId)
    }
    @Test fun quantityCanExceedIntWithoutOverflow() {
        val orders=(1L..2L).map { id -> PedidoConsultaSnapshot(id,1000,Money(Int.MAX_VALUE.toLong()),listOf(line(id,7,Int.MAX_VALUE,1))) }
        assertEquals(4294967294L,rank(orders).single().cantidad)
    }
    @Test fun realOtrosIsNotARemainderBucket() { assertEquals("Otros",rank(listOf(PedidoConsultaSnapshot(1,1000,Money(100),listOf(line(quantity=1).copy(nombreHistorico="Otros"))))).single().nombreHistorico) }
}
