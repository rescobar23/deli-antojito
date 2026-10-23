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

class OrderFiltersScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun filterDraftSurvivesStateRestorationBeforeApply() {
        var applied: PedidoConsulta? = null
        val restoration = androidx.compose.ui.test.junit4.StateRestorationTester(compose)
        restoration.setContent { OrderFilters(PedidoConsulta(), { applied = it }, {}) }
        compose.onNodeWithTag("order_filter_state_PENDIENTE").performScrollTo().performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("order_filter_state_PENDIENTE").performScrollTo().assertIsOn()
        compose.onNodeWithText("Aplicar").performClick()
        assertEquals(setOf(EstadoPedido.PENDIENTE), applied?.estadosAvanzados)
    }
    @Test fun invalidRangeDoesNotApplyAndClearResetsOnlyAdvancedFields() {
        var applied: PedidoConsulta? = null
        compose.setContent { OrderFilters(PedidoConsulta(texto = "María", estadoPestana = EstadoPedido.PENDIENTE), { applied = it }, {}) }
        compose.onNodeWithText("Importe mínimo").performTextInput("100")
        compose.onNodeWithText("Importe máximo").performTextInput("10")
        compose.onNodeWithText("Aplicar").performClick()
        assertNull(applied)
        compose.onNodeWithText("Limpiar").performScrollTo().performClick()
        compose.onNodeWithText("Aplicar").performClick()
        assertEquals("María", applied?.texto)
        assertEquals(EstadoPedido.PENDIENTE, applied?.estadoPestana)
        assertNull(applied?.minimo)
    }
}

