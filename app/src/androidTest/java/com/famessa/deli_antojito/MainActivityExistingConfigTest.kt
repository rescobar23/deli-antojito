package com.famessa.deli_antojito

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.famessa.deli_antojito.feature.home.HomeContent
import org.junit.Rule
import org.junit.Test

class MainActivityExistingConfigTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun existingConfigurationShowsHomeContent() {
        composeRule.setContent {
            HomeContent(businessName = "Deli Guardado")
        }

        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("business_config_screen").assertDoesNotExist()
    }
}
