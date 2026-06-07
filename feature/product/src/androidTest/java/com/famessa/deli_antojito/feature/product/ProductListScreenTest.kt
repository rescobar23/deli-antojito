package com.famessa.deli_antojito.feature.product

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.famessa.deli_antojito.domain.model.ProductListSummary
import com.famessa.deli_antojito.domain.model.Producto
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyStateIsDisplayed() {
        composeRule.setContent {
            ProductListScreen(
                state = ProductListUiState.Empty(),
                onAddProduct = {},
                onEditProduct = {},
                onRequestDelete = {},
                onConfirmDelete = {},
                onCancelDelete = {},
                onRetry = {},
                onBack = {}
            )
        }

        composeRule.onNodeWithTag(ProductTestTags.EmptyState).assertIsDisplayed()
    }

    @Test
    fun loadedProductIsDisplayed() {
        composeRule.setContent {
            ProductListScreen(
                state = ProductListUiState.Loaded(
                    productos = listOf(Producto(id = 1, nombre = "Alitas", precioBase = 120.0)),
                    summary = ProductListSummary(totalProductos = 1, totalActivos = 1)
                ),
                onAddProduct = {},
                onEditProduct = {},
                onRequestDelete = {},
                onConfirmDelete = {},
                onCancelDelete = {},
                onRetry = {},
                onBack = {}
            )
        }

        composeRule.onNodeWithText("Alitas").assertIsDisplayed()
    }

    @Test
    fun deleteConfirmationIsDisplayed() {
        val product = Producto(id = 1, nombre = "Alitas", precioBase = 120.0)
        composeRule.setContent {
            ProductListScreen(
                state = ProductListUiState.Loaded(
                    productos = listOf(product),
                    summary = ProductListSummary(totalProductos = 1, totalActivos = 1),
                    pendingDelete = product
                ),
                onAddProduct = {},
                onEditProduct = {},
                onRequestDelete = {},
                onConfirmDelete = {},
                onCancelDelete = {},
                onRetry = {},
                onBack = {}
            )
        }

        composeRule.onNodeWithTag(ProductTestTags.DeleteConfirm).assertIsDisplayed()
    }
}
