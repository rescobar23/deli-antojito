package com.famessa.deli_antojito.feature.product

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductListRoute(
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ProductListScreen(
        state = state,
        onAddProduct = onAddProduct,
        onEditProduct = onEditProduct,
        onRequestDelete = viewModel::requestDelete,
        onConfirmDelete = viewModel::confirmDelete,
        onCancelDelete = viewModel::cancelDelete,
        onRetry = viewModel::retry,
        onBack = onBack
    )
}
