package com.famessa.deli_antojito.data.mappers

import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.EstadoPedido
import com.famessa.deli_antojito.domain.model.PedidoLinea
import com.famessa.deli_antojito.domain.model.PedidoResumen

/** Validates persisted values rather than silently repairing corrupt history. */
fun Pedido.toDomain(lines: List<PedidoDetalle>): com.famessa.deli_antojito.domain.model.Pedido {
    require(id > 0 && numero > 0 && revision > 0 && cliente.isNotBlank() && lines.isNotEmpty())
    val mapped = lines.sortedWith(compareBy<PedidoDetalle> { it.posicion }.thenBy { it.id }).map { line ->
        require(line.id > 0 && line.pedidoId == id && line.productoId > 0 && line.nombreProducto.isNotBlank())
        require(line.cantidad > 0 && line.precioUnitarioCentavos > 0 && line.posicion >= 0)
        val price = Money(line.precioUnitarioCentavos)
        require((price * line.cantidad).centavos == line.subtotalCentavos)
        PedidoLinea(line.id, line.productoId, line.nombreProducto, line.cantidad, price, Money(line.subtotalCentavos), line.posicion)
    }
    require(mapped.fold(Money(0)) { total, line -> total + line.subtotal }.centavos == totalCentavos)
    return com.famessa.deli_antojito.domain.model.Pedido(id, numero, cliente, fechaCreacion, fechaActualizacion,
        EstadoPedido.valueOf(estado), Money(totalCentavos), observaciones, revision, mapped)
}

/** Maps only card fields; no catalog lookup or image payload is required. */
fun Pedido.toSummary(): PedidoResumen {
    require(id > 0 && numero > 0 && revision > 0 && cliente.isNotBlank() && totalCentavos > 0)
    return PedidoResumen(id, numero, cliente, fechaCreacion, EstadoPedido.valueOf(estado), Money(totalCentavos))
}
