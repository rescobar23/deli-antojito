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

class OrdersAccessibilityTest {
    @get:Rule val compose = createComposeRule()
    @Test fun summaryRemainsReachableAtLargeFont() {
        compose.setContent {
            val density = androidx.compose.ui.platform.LocalDensity.current
            CompositionLocalProvider(androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density, 2f)) {
                OrdersScreen(OrdersUiState(loading = false), summary = OrdersSummaryState(ResumenPedidos(com.famessa.deli_antojito.domain.time.LocalDateParts(2026,10,21), ResumenConteos(0,0,0,0))))
            }
        }
        compose.onNodeWithTag("orders_summary").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("orders_add").assertHasClickAction()
    }
}

