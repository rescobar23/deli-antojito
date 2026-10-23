package com.famessa.deli_antojito.feature.orders.form

import com.famessa.deli_antojito.domain.model.*

data class OrderFormLine(val key: String, val existingId: Long? = null, val snapshot: PedidoProductSnapshot, val quantity: String)
data class ProductChoice(val id: Long, val name: String, val price: Money)
data class OrderFormState(
    val entryKey: String? = null,
    val createdAt: Long? = null,
    val status: EstadoPedido = EstadoPedido.PENDIENTE,
    val loading: Boolean = false,
    val cliente: String = "",
    val observaciones: String = "",
    val lines: List<OrderFormLine> = emptyList(),
    val products: List<ProductChoice> = emptyList(),
    val catalogError: String? = null,
    val total: Money? = Money(0),
    val error: String? = null,
    val dirty: Boolean = false,
    val saving: Boolean = false,
    val capturing: Boolean = false,
    val saved: Boolean = false,
    val editable: Boolean = true
)
