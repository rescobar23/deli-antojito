package com.famessa.deli_antojito.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeBusinessConfigTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun displaysSavedBusinessName() {
        composeRule.setContent {
            HomeContent(businessName = "Deli Guardado")
        }

        composeRule.onNodeWithTag("home_business_name").assertIsDisplayed()
    }

    @Test
    fun configurationEntryPointIsClickable() {
        var clicked = false
        composeRule.setContent {
            HomeContent(
                businessName = "Deli Guardado",
                onAdminClick = { clicked = true }
            )
        }

        composeRule.onNodeWithTag("home_business_config_button").performClick()

        assertEquals(true, clicked)
    }
}
