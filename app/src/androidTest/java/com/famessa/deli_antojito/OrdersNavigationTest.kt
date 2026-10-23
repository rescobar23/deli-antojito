package com.famessa.deli_antojito

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import com.famessa.deli_antojito.feature.home.HomeContent
import com.famessa.deli_antojito.navigation.*
import org.junit.Rule
import org.junit.Test

class OrdersNavigationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun homeOrdersCardTriggersOrderDestination() {
        compose.setContent {
            val navigation = rememberMainNavigationState()
            if (navigation.destination == MainDestination.Home)
                HomeContent("Negocio fixture", onOrdersClick = { navigation.section("orders") })
            else Text("Destino pedidos")
        }
        compose.onNodeWithText("Pedidos").performScrollTo().performClick()
        compose.onNodeWithText("Destino pedidos").assertIsDisplayed()
    }
}
