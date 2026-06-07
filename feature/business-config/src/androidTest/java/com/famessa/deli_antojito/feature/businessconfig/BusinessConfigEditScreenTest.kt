package com.famessa.deli_antojito.feature.businessconfig

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BusinessConfigEditScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun editModePrefillsName() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(isInitialSetup = false, businessName = "Deli Actual"),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag(BusinessConfigTestTags.NameInput).assertTextContains("Deli Actual")
    }

    @Test
    fun editModeShowsInvalidUpdateMessage() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(
                    isInitialSetup = false,
                    validationMessage = "Captura un nombre de negocio valido."
                ),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithText("Captura un nombre de negocio valido.").assertIsDisplayed()
    }

    @Test
    fun editModeSaveActionIsInvoked() {
        var saved = false
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(isInitialSetup = false, businessName = "Nuevo"),
                onNameChange = {},
                onSave = { saved = true },
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag(BusinessConfigTestTags.SaveButton).performClick()

        assertEquals(true, saved)
    }

    @Test
    fun persistenceErrorFeedbackIsDisplayed() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(
                    isInitialSetup = false,
                    businessName = "Nuevo",
                    errorMessage = "No se pudo guardar. Intenta de nuevo."
                ),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag(BusinessConfigTestTags.Error).assertIsDisplayed()
    }
}
