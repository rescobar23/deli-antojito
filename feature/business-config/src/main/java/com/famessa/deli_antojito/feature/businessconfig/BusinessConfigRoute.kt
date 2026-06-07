package com.famessa.deli_antojito.feature.businessconfig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BusinessConfigRoute(
    isInitialSetup: Boolean,
    onSaved: () -> Unit,
    onCancel: () -> Unit = {},
    viewModel: BusinessConfigViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(isInitialSetup) {
        viewModel.start(isInitialSetup)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    BusinessConfigScreen(
        state = state,
        onNameChange = viewModel::onBusinessNameChange,
        onSave = viewModel::save,
        onRetry = viewModel::retry,
        onCancel = onCancel
    )
}
