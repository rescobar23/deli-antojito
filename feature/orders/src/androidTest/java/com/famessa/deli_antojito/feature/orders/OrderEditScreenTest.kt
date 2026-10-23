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

class OrderEditScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun terminalContentCannotBeSaved() {
        compose.setContent { OrderFormScreen(OrderFormState(editable = false), true, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}) }
        compose.onNodeWithTag("order_save").performScrollTo().assertIsNotEnabled()
    }
    @Test fun discardRequiresExplicitChoice() {
        var confirmed = false
        compose.setContent { OrderDiscardDialog({ confirmed = true }, {}) }
        compose.onNodeWithText("Continuar editando").performClick()
        assertFalse(confirmed)
        compose.onNodeWithText("Descartar cambios").performClick()
        assertTrue(confirmed)
    }
}

