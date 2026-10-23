package com.famessa.deli_antojito.domain.model.cierre

import com.famessa.deli_antojito.domain.model.Money

/** Minimal historical header without customer data; lines belong to the same storage snapshot. */
data class PedidoConsultaSnapshot(val pedidoId: Long, val fechaCreacion: Long, val totalBruto: Money, val lineas: List<LineaConsultaSnapshot>)
/** Historical product line, independent of current catalog contents. */
data class LineaConsultaSnapshot(val lineaId: Long, val productoId: Long, val nombreHistorico: String,
    val cantidad: Int, val precioUnitario: Money, val subtotal: Money, val posicion: Int)
