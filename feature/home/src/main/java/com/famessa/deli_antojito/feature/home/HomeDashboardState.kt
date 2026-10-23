package com.famessa.deli_antojito.feature.home

import com.famessa.deli_antojito.domain.model.PedidoResumen
import com.famessa.deli_antojito.domain.model.ResumenPedidos

/** Independently loaded recent orders and daily counts for the home dashboard. */
data class HomeDashboardState(
    val recentOrders: List<PedidoResumen> = emptyList(),
    val loadingOrders: Boolean = true,
    val ordersError: String? = null,
    val summary: ResumenPedidos? = null,
    val summaryError: String? = null
)
