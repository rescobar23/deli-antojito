package com.famessa.deli_antojito.feature.product

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ProductEditScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun addFormIsDisplayed() {
        composeRule.setContent {
            ProductEditScreen(
                state = ProductFormState(),
                isEdit = false,
                onNameChange = {},
                onPriceChange = {},
                onActiveChange = {},
                onRemoveImage = {},
                onSave = {},
                onBack = {},
                onConfirmDiscard = {},
                onCancelDiscard = {}
            )
        }

        composeRule.onNodeWithTag(ProductTestTags.EditScreen).assertIsDisplayed()
        composeRule.onNodeWithText("Agregar producto").assertIsDisplayed()
    }

    @Test
    fun validationMessageIsDisplayed() {
        composeRule.setContent {
            ProductEditScreen(
                state = ProductFormState(validationMessage = "El precio base debe ser mayor a cero."),
                isEdit = false,
                onNameChange = {},
                onPriceChange = {},
                onActiveChange = {},
                onRemoveImage = {},
                onSave = {},
                onBack = {},
                onConfirmDiscard = {},
                onCancelDiscard = {}
            )
        }

        composeRule.onNodeWithTag(ProductTestTags.Validation).assertIsDisplayed()
    }

    @Test
    fun editTitleIsDisplayed() {
        composeRule.setContent {
            ProductEditScreen(
                state = ProductFormState(id = 1, nombre = "Alitas", precioBase = "120"),
                isEdit = true,
                onNameChange = {},
                onPriceChange = {},
                onActiveChange = {},
                onRemoveImage = {},
                onSave = {},
                onBack = {},
                onConfirmDiscard = {},
                onCancelDiscard = {}
            )
        }

        composeRule.onNodeWithText("Editar producto").assertIsDisplayed()
    }
}
