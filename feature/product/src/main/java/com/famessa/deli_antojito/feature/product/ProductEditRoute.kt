package com.famessa.deli_antojito.feature.product

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductEditRoute(
    productId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ProductEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.start(productId)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    ProductEditScreen(
        state = state,
        isEdit = productId != null && productId > 0L,
        onNameChange = viewModel::onNameChange,
        onPriceChange = viewModel::onPriceChange,
        onActiveChange = viewModel::onActiveChange,
        onRemoveImage = viewModel::removeImage,
        onSave = viewModel::save,
        onBack = { viewModel.requestNavigateBack(onCancel) },
        onConfirmDiscard = { viewModel.confirmDiscard(onCancel) },
        onCancelDiscard = viewModel::cancelDiscard
    )
}
