package com.famessa.deli_antojito

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Column
import com.famessa.deli_antojito.navigation.*
import org.junit.Rule
import org.junit.Test

class OrdersRestorationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun backStackRestoresDetailIdentityAndParentList() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            val state = rememberMainNavigationState()
            Column {
                Text(state.stack.last())
                Button(onClick = { state.section("orders"); state.navigate("detail:25") }) { Text("Abrir pedido") }
                Button(onClick = { state.back() }) { Text("Volver") }
            }
        }
        compose.onNodeWithText("Abrir pedido").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("detail:25").assertExists()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("orders").assertExists()
    }
}
