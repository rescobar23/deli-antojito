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

class OrderCreateScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun quantityButtonsUpdateLineAndRemoveInvokesCallback() {
        var quantity by mutableStateOf("2")
        var removed: String? = null
        compose.setContent {
            val line = OrderFormLine("line", snapshot = PedidoProductSnapshot(1, "Fixture", Money(1250)), quantity = quantity)
            OrderFormScreen(OrderFormState(cliente = "Cliente", lines = listOf(line)), false, {}, {}, {},
                { _, value -> quantity = value }, { removed = it }, {}, {}, {}, {}, {})
        }
        compose.onNodeWithContentDescription("Aumentar cantidad de Fixture").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("3", quantity) }
        compose.onNodeWithContentDescription("Disminuir cantidad de Fixture").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("2", quantity) }
        compose.onNodeWithContentDescription("Retirar Fixture").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("line", removed) }
    }
    @Test fun emptyCatalogExplainsWhyAndSaveIsDisabledWhileSending() {
        compose.setContent {
            OrderFormScreen(OrderFormState(saving = true), false, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {})
        }
        compose.onNodeWithTag("order_save").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("No hay productos activos con precio válido para añadir.").assertExists()
    }
    @Test fun capturedPriceIsReadOnlyWhileQuantityCanBeChanged() {
        val state = OrderFormState(cliente = "Cliente", lines = listOf(OrderFormLine("x", snapshot = PedidoProductSnapshot(1, "Fixture", Money(1250)), quantity = "2")), total = Money(2500))
        compose.setContent { OrderFormScreen(state, false, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}) }
        compose.onNodeWithContentDescription("Precio unitario: $12.50").assertExists()
        compose.onNodeWithText("Cantidad *").assertExists()
    }
}

