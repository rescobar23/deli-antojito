package com.famessa.deli_antojito.domain.model

import com.famessa.deli_antojito.domain.time.LocalDateParts

/** Independent criteria intersect; an empty advanced selection means no advanced state restriction. */
data class PedidoConsulta(
    val texto: String = "",
    val estadoPestana: EstadoPedido? = null,
    val estadosAvanzados: Set<EstadoPedido> = emptySet(),
    val desde: LocalDateParts? = null,
    val hasta: LocalDateParts? = null,
    val minimo: Money? = null,
    val maximo: Money? = null
)

/** Daily counts with the civil date used to compute their creation-time interval. */
data class ResumenPedidos(val fecha: LocalDateParts, val conteos: ResumenConteos)
