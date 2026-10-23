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

class OrderStateScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun pendingOrderRetainsFieldsAndTransitionActions() {
        val order = Pedido(1, 25, "Cliente fixture", 1, 2, EstadoPedido.PENDIENTE, Money(2500), "Nota fixture", 0,
            listOf(PedidoLinea(1, 1, "Producto fixture", 2, Money(1250), Money(2500), 0)))
        var edited = false
        var target: EstadoPedido? = null
        compose.setContent { OrderDetailScreen(OrderDetailState(loading = false, pedido = order), {}, { edited = true }, { target = it }, {}) }
        compose.onNodeWithText("Cliente fixture").assertExists()
        compose.onNodeWithText("Creado").assertExists()
        compose.onNodeWithText("Actualizado").assertExists()
        compose.onNodeWithText("Producto fixture").assertExists()
        compose.onNodeWithText("Nota fixture").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("order_detail_edit").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(edited) }
        compose.onNodeWithTag("order_detail_prepare").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(EstadoPedido.EN_PREPARACION, target) }
        compose.onNodeWithTag("order_detail_cancel").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(EstadoPedido.CANCELADO, target) }
    }

    @Test fun busyOrderDisablesTransitionActions() {
        val order = Pedido(1, 1, "Cliente", 1, 1, EstadoPedido.EN_PREPARACION, Money(100), null, 1, emptyList())
        compose.setContent { OrderDetailScreen(OrderDetailState(loading = false, pedido = order, busy = true), {}, {}, {}, {}) }
        compose.onNodeWithTag("order_detail_complete").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithTag("order_detail_cancel").performScrollTo().assertIsNotEnabled()
    }
    @Test fun completedOrderHasNoMutatingActions() {
        val order = Pedido(1, 1, "Cliente", 1, 1, EstadoPedido.COMPLETADO, Money(100), null, 3, emptyList())
        compose.setContent { OrderDetailScreen(OrderDetailState(loading = false, pedido = order), {}, {}, {}, {}) }
        compose.onNodeWithText("Editar").assertDoesNotExist()
        compose.onNodeWithText("Cancelar pedido").assertDoesNotExist()
        compose.onNodeWithText("Completar").assertDoesNotExist()
    }
    @Test fun cancellationTextAndConfirmationAreExplicit() {
        var confirmed = false
        compose.setContent { OrderCancelDialog({ confirmed = true }, {}) }
        compose.onNodeWithText("¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.").assertExists()
        compose.onNodeWithTag("order_cancel_confirm").performClick()
        assertTrue(confirmed)
    }
}

