package com.famessa.deli_antojito.feature.product

import com.famessa.deli_antojito.domain.model.ProductListSummary
import com.famessa.deli_antojito.domain.model.Producto

object ProductTestTags {
    const val ListScreen = "product_list_screen"
    const val EmptyState = "product_empty_state"
    const val AddButton = "product_add_button"
    const val RetryButton = "product_retry_button"
    const val EditScreen = "product_edit_screen"
    const val ImagePicker = "product_image_picker"
    const val ImageSourceDialog = "product_image_source_dialog"
    const val NameInput = "product_name_input"
    const val PriceInput = "product_price_input"
    const val ActiveSwitch = "product_active_switch"
    const val SaveButton = "product_save_button"
    const val Validation = "product_validation"
    const val DeleteConfirm = "product_delete_confirm"
}

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data class Empty(val summary: ProductListSummary = ProductListSummary()) : ProductListUiState
    data class Loaded(
        val productos: List<Producto>,
        val summary: ProductListSummary,
        val pendingDelete: Producto? = null,
        val errorMessage: String? = null
    ) : ProductListUiState
    data class Error(val message: String) : ProductListUiState
}

data class ProductFormState(
    val id: Long = 0,
    val nombre: String = "",
    val precioBase: String = "",
    val img: String? = null,
    val activo: Boolean = true,
    val validationMessage: String? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDirty: Boolean = false,
    val showDiscardConfirmation: Boolean = false,
    val showImageSourceChooser: Boolean = false
)
