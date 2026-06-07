package com.famessa.deli_antojito.feature.businessconfig

data class BusinessConfigUiState(
    val businessName: String = "",
    val isInitialSetup: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val validationMessage: String? = null,
    val errorMessage: String? = null,
    val isInvalidLocalData: Boolean = false
)
