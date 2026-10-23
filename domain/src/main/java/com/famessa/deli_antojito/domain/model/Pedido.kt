package com.famessa.deli_antojito.domain.model

/** Persisted order state. Completed and cancelled orders are terminal. */
enum class EstadoPedido { PENDIENTE, EN_PREPARACION, COMPLETADO, CANCELADO }

/** Historical product snapshot; catalog deletion must never change this line. */
data class PedidoLinea(
    val id: Long,
    val productoId: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Money,
    val subtotal: Money,
    val posicion: Int
)

/** Immutable order aggregate; timestamps are epoch milliseconds and revision protects concurrent edits. */
data class Pedido(
    val id: Long,
    val numero: Long,
    val cliente: String,
    val fechaCreacion: Long,
    val fechaActualizacion: Long,
    val estado: EstadoPedido,
    val total: Money,
    val observaciones: String?,
    val revision: Long,
    val lineas: List<PedidoLinea>
)

/** Lightweight list projection, deliberately excluding line details and catalog images. */
data class PedidoResumen(
    val id: Long,
    val numero: Long,
    val cliente: String,
    val fechaCreacion: Long,
    val estado: EstadoPedido,
    val total: Money
)

/** Append-only state transition, ordered by id rather than wall-clock monotonicity. */
data class PedidoTransicion(
    val id: Long,
    val pedidoId: Long,
    val anterior: EstadoPedido,
    val nuevo: EstadoPedido,
    val ocurridoEn: Long
)

/** Counts over orders created within a day, including cancelled orders in the total. */
data class ResumenConteos(
    val totalPedidos: Int,
    val pendientes: Int,
    val enPreparacion: Int,
    val completados: Int
)
