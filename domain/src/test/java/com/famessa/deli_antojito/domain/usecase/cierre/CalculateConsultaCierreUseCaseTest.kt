package com.famessa.deli_antojito.domain.usecase.cierre
import org.junit.Test
import org.junit.Assert.*
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*

class CalculateConsultaCierreUseCaseTest {
    private val calculate = CalculateConsultaCierreUseCase()
    @Test fun distinctHeadersAreNotMultipliedByTheirLines() {
        val result = calculate(period, fixture()) as ConsultaCierreResult.Success
        assertEquals(2, result.consulta.resumen.totalPedidos)
        assertEquals(500, result.consulta.resumen.bruto.centavos)
    }
    @Test fun emptyIsAValidZero() { assertEquals(0, (calculate(period, emptyList()) as ConsultaCierreResult.Success).consulta.resumen.totalPedidos) }
    @Test fun missingLinesIsNotSilentlyExcluded() { invalid(fixture()[0].copy(lineas = emptyList()), CierreDataError.MissingLines) }
    @Test fun duplicateLinesRejected() { invalid(fixture()[0].copy(lineas = listOf(line(), line())), CierreDataError.DuplicateLine) }
    @Test fun duplicatePositionRejected() { invalid(fixture()[0].copy(lineas = listOf(line(), line(2).copy(posicion = 0))), CierreDataError.DuplicatePosition) }
    @Test fun invalidIdentityRejected() { invalid(fixture()[0].copy(pedidoId = 0), CierreDataError.InvalidIdentity) }
    @Test fun invalidQuantityRejected() { invalid(fixture()[0].copy(lineas = listOf(line().copy(cantidad = 0))), CierreDataError.InvalidQuantity) }
    @Test fun invalidNameRejected() { invalid(fixture()[0].copy(lineas = listOf(line().copy(nombreHistorico = ""))), CierreDataError.InvalidIdentity) }
    @Test fun wrongSubtotalRejected() { invalid(fixture()[0].copy(lineas = listOf(line().copy(subtotal = Money(1)))), CierreDataError.LineSubtotalMismatch) }
    @Test fun wrongHeaderRejected() { invalid(fixture()[0].copy(totalBruto = Money(1)), CierreDataError.HeaderTotalMismatch) }
    @Test fun overflowRejected() {
        val huge = line(quantity = 1, cents = Long.MAX_VALUE)
        val orders = listOf(PedidoConsultaSnapshot(1, 1000, Money(Long.MAX_VALUE), listOf(huge)), PedidoConsultaSnapshot(2, 1500, Money(Long.MAX_VALUE), listOf(huge.copy(lineaId = 2))))
        assertEquals(ConsultaCierreResult.InvalidData(CierreDataError.Overflow), calculate(period, orders))
    }
    private fun invalid(order: PedidoConsultaSnapshot, code: CierreDataError) { assertEquals(ConsultaCierreResult.InvalidData(code), calculate(period, listOf(order))) }
}
