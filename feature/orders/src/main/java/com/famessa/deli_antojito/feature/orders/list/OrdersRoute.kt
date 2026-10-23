package com.famessa.deli_antojito.feature.orders.list

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.famessa.deli_antojito.feature.orders.components.collectOrderState

@Composable fun OrdersRoute(onAdd: () -> Unit, onOpen: (Long) -> Unit, onBack: () -> Unit) {
    val model: OrdersViewModel = hiltViewModel(key = "orders-list")
    val summary: OrdersSummaryViewModel = hiltViewModel(key = "orders-summary")
    val state by model.uiState.collectOrderState()
    val summaryState by summary.uiState.collectOrderState()
    OrdersScreen(state, onAdd, onOpen, model::search, model::selectTab, model::applyFilters, model::retry,
        summaryState, summary::retry, onBack)
}
