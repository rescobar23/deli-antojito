package com.famessa.deli_antojito.feature.orders

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.famessa.deli_antojito.feature.orders.list.OrdersScreen
import com.famessa.deli_antojito.feature.orders.list.OrdersUiState
import com.famessa.deli_antojito.domain.model.*
import org.junit.Rule
import org.junit.Test

class OrdersReadScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun rendersEmptyAndAddAction() {
        compose.setContent { OrdersScreen(OrdersUiState(loading = false)) }
        compose.onNodeWithTag("orders_empty").assertExists()
        compose.onNodeWithTag("orders_add").assertExists()
    }
    @Test fun showsUntruncatedConsecutiveAndClickableCard() {
        compose.setContent {
            OrdersScreen(OrdersUiState(loading = false, pedidos = listOf(
                PedidoResumen(1, 100000, "Cliente fixture", 1, EstadoPedido.PENDIENTE, Money(1250))
            )))
        }
        compose.onNodeWithText("#100000", substring = true).assertExists()
        compose.onNodeWithTag("order_card_1").assertHasClickAction()
        compose.onNodeWithText("Cliente fixture").assertExists()
        compose.onNodeWithText("Pendiente").assertExists()
        compose.onNodeWithText("$12.50").assertExists()
    }
    @Test fun readErrorOffersRetryWithoutPretendingStoreIsEmpty() {
        var retried = false
        compose.setContent { OrdersScreen(OrdersUiState(loading = false, error = "Error de lectura",
            pedidos = listOf(PedidoResumen(1,1,"Cliente",1,EstadoPedido.PENDIENTE,Money(100)))), onRetry = { retried = true }) }
        compose.onNodeWithTag("orders_empty").assertDoesNotExist()
        compose.onNodeWithText("Los resultados visibles pueden estar desactualizados.").assertExists()
        compose.onNodeWithTag("orders_retry").performClick()
        org.junit.Assert.assertTrue(retried)
    }
}
