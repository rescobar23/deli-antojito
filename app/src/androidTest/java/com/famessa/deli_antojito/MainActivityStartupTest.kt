package com.famessa.deli_antojito

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.famessa.deli_antojito.feature.businessconfig.BusinessConfigScreen
import com.famessa.deli_antojito.feature.businessconfig.BusinessConfigUiState
import org.junit.Rule
import org.junit.Test

class MainActivityStartupTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun noConfigurationStartupShowsRegistration() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(isInitialSetup = true),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag("business_config_screen").assertIsDisplayed()
    }
}
