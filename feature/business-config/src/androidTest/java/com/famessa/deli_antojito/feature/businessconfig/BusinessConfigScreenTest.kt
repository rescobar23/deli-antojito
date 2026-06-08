package com.famessa.deli_antojito.feature.businessconfig

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BusinessConfigScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun registrationFormIsDisplayed() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(isInitialSetup = true),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithText("Configuración").assertIsDisplayed()
        composeRule.onNodeWithTag(BusinessConfigTestTags.NameInput).assertIsDisplayed()
    }

    @Test
    fun invalidNameFeedbackIsDisplayed() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(validationMessage = "El nombre del negocio es obligatorio."),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag(BusinessConfigTestTags.Validation).assertIsDisplayed()
    }

    @Test
    fun registrationDoesNotShowCancelBypass() {
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(isInitialSetup = true),
                onNameChange = {},
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag(BusinessConfigTestTags.CancelButton).assertDoesNotExist()
    }

    @Test
    fun saveActionIsInvoked() {
        var saved = false
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(businessName = "Deli"),
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
    fun textInputCallsChangeCallback() {
        var typed = ""
        composeRule.setContent {
            BusinessConfigScreen(
                state = BusinessConfigUiState(),
                onNameChange = { typed = it },
                onSave = {},
                onRetry = {},
                onCancel = {}
            )
        }

        composeRule.onNodeWithTag(BusinessConfigTestTags.NameInput).performTextInput("Deli")

        assertEquals("Deli", typed)
    }
}
