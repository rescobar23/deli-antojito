package com.famessa.deli_antojito.feature.orders
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.*
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.feature.orders.form.*
import com.famessa.deli_antojito.feature.orders.detail.*
import com.famessa.deli_antojito.feature.orders.list.*
import com.famessa.deli_antojito.feature.orders.components.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class OrderDailySummaryScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun summaryShowsFourCountsIncludingCancelledOnlyInTotal() {
        compose.setContent { OrdersDailySummary(OrdersSummaryState(ResumenPedidos(com.famessa.deli_antojito.domain.time.LocalDateParts(2026,10,21), ResumenConteos(5,2,1,1))), {}) }
        compose.onNodeWithContentDescription("Pedidos: 5").assertExists()
        compose.onNodeWithContentDescription("Pendientes: 2").assertExists()
        compose.onNodeWithContentDescription("En preparación: 1").assertExists()
        compose.onNodeWithContentDescription("Completados: 1").assertExists()
    }
}

