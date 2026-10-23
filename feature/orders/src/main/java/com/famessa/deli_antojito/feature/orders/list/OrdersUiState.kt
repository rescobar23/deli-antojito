package com.famessa.deli_antojito.feature.orders.list

import com.famessa.deli_antojito.domain.model.*

data class OrdersUiState(
    val loading: Boolean = true,
    val pedidos: List<PedidoResumen> = emptyList(),
    val total: Int = 0,
    val query: PedidoConsulta = PedidoConsulta(),
    val error: String? = null
)
