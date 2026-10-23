package com.famessa.deli_antojito.domain.usecase.cierre
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*

/** Pure exact aggregation of one consistent historical snapshot. Never silently omits corrupt orders. */
class CalculateConsultaCierreUseCase {
    /** Returns both indicators together, or a typed integrity error with no partial totals. */
    operator fun invoke(periodo: CierrePeriodo, pedidos: List<PedidoConsultaSnapshot>): ConsultaCierreResult {
        fun invalid(code: CierreDataError) = ConsultaCierreResult.InvalidData(code)
        val orderIds = mutableSetOf<Long>()
        val lineIds = mutableSetOf<Long>()
        var gross = Money(0)
        try {
            for (order in pedidos) {
                if (order.pedidoId <= 0 || !orderIds.add(order.pedidoId)) return invalid(CierreDataError.InvalidIdentity)
                if (order.lineas.isEmpty()) return invalid(CierreDataError.MissingLines)
                val positions = mutableSetOf<Int>()
                var lineTotal = Money(0)
                for (line in order.lineas) {
                    if (line.lineaId <= 0 || line.productoId <= 0 || line.nombreHistorico.isBlank() || line.posicion < 0) return invalid(CierreDataError.InvalidIdentity)
                    if (!lineIds.add(line.lineaId)) return invalid(CierreDataError.DuplicateLine)
                    if (!positions.add(line.posicion)) return invalid(CierreDataError.DuplicatePosition)
                    if (line.cantidad <= 0) return invalid(CierreDataError.InvalidQuantity)
                    if (line.precioUnitario * line.cantidad != line.subtotal) return invalid(CierreDataError.LineSubtotalMismatch)
                    lineTotal += line.subtotal
                }
                if (lineTotal != order.totalBruto) return invalid(CierreDataError.HeaderTotalMismatch)
                gross += order.totalBruto
            }
            val historicalLines = pedidos.flatMap { order -> order.lineas.map { line -> order to line } }
            val products = historicalLines.groupBy { it.second.productoId }.map { (productId, rows) ->
                val latest = rows.maxWith(compareBy<Pair<PedidoConsultaSnapshot, LineaConsultaSnapshot>> { it.first.fechaCreacion }
                    .thenBy { it.first.pedidoId }.thenBy { it.second.posicion })
                var quantity = 0L
                var amount = Money(0)
                rows.forEach { (_, line) ->
                    require(quantity <= Long.MAX_VALUE - line.cantidad)
                    quantity += line.cantidad
                    amount += line.subtotal
                }
                ProductoConsultaCierre(productId, latest.second.nombreHistorico, quantity, amount)
            }.sortedWith(compareByDescending<ProductoConsultaCierre> { it.cantidad }.thenByDescending { it.bruto.centavos }.thenBy { it.productoId }).take(5)
            return ConsultaCierreResult.Success(ConsultaCierre(periodo, ResumenConsultaCierre(orderIds.size, gross), products))
        } catch (_: IllegalArgumentException) { return invalid(CierreDataError.Overflow) }
    }
}

