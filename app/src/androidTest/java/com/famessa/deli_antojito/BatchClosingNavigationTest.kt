package com.famessa.deli_antojito
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.material3.Text
import com.famessa.deli_antojito.feature.home.HomeContent
import com.famessa.deli_antojito.navigation.*
import org.junit.Rule
import org.junit.Test
class BatchClosingNavigationTest {
    @get:Rule val compose=createComposeRule()
    @Test fun homeCardOpensDistinctDestination() {
        compose.setContent {
            val navigation=rememberMainNavigationState()
            if(navigation.destination==MainDestination.Home) HomeContent("Negocio fixture",onBatchClosingClick={navigation.section("close")})
            else Text("Consulta de lote")
        }
        compose.onNodeWithTag("home_close_button").performScrollTo().performClick()
        compose.onNodeWithText("Consulta de lote").assertIsDisplayed()
    }
}
