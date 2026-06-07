package com.famessa.deli_antojito.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import com.famessa.deli_antojito.domain.model.ProductoValidationResult
import com.famessa.deli_antojito.domain.usecase.producto.GetProductoByIdUseCase
import com.famessa.deli_antojito.domain.usecase.producto.SaveProductoUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ValidateProductoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductEditViewModel @Inject constructor(
    private val getProductoByIdUseCase: GetProductoByIdUseCase,
    private val saveProductoUseCase: SaveProductoUseCase,
    private val validateProductoUseCase: ValidateProductoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormState())
    val uiState: StateFlow<ProductFormState> = _uiState.asStateFlow()

    fun start(productId: Long?) {
        if (productId == null || productId == 0L) {
            _uiState.value = ProductFormState(activo = true)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val producto = getProductoByIdUseCase(productId)
            _uiState.value = if (producto == null) {
                ProductFormState(errorMessage = "El producto no existe.")
            } else {
                ProductFormState(
                    id = producto.id,
                    nombre = producto.nombre,
                    precioBase = producto.precioBase.toString(),
                    img = producto.img,
                    activo = producto.activo
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            nombre = value,
            validationMessage = null,
            errorMessage = null,
            isDirty = true
        )
    }

    fun onPriceChange(value: String) {
        _uiState.value = _uiState.value.copy(
            precioBase = value,
            validationMessage = null,
            errorMessage = null,
            isDirty = true
        )
    }

    fun onActiveChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(activo = value, isDirty = true)
    }

    fun onImageSelected(base64: String?, mimeType: String?, sizeBytes: Long?) {
        val validation = validateProductoUseCase(
            nombre = _uiState.value.nombre.ifBlank { "Temporal" },
            precioBase = _uiState.value.precioBase.toDoubleOrNull() ?: 1.0,
            imageMimeType = mimeType,
            imageSizeBytes = sizeBytes
        )
        if (validation == ProductoValidationResult.InvalidImageFormat || validation == ProductoValidationResult.ImageTooLarge) {
            _uiState.value = _uiState.value.copy(validationMessage = validation.toMessage())
            return
        }
        _uiState.value = _uiState.value.copy(img = base64, isDirty = true, validationMessage = null)
    }

    fun removeImage() {
        _uiState.value = _uiState.value.copy(img = null, isDirty = true)
    }

    fun requestNavigateBack(onCleanBack: () -> Unit) {
        if (_uiState.value.isDirty) {
            _uiState.value = _uiState.value.copy(showDiscardConfirmation = true)
        } else {
            onCleanBack()
        }
    }

    fun cancelDiscard() {
        _uiState.value = _uiState.value.copy(showDiscardConfirmation = false)
    }

    fun confirmDiscard(onDiscarded: () -> Unit) {
        _uiState.value = _uiState.value.copy(showDiscardConfirmation = false)
        onDiscarded()
    }

    fun save() {
        val current = _uiState.value
        val price = current.precioBase.toDoubleOrNull()
        val validation = validateProductoUseCase(current.nombre, price)
        if (validation !is ProductoValidationResult.Valid) {
            _uiState.value = current.copy(validationMessage = validation.toMessage())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, validationMessage = null, errorMessage = null)
            val result = saveProductoUseCase(
                Producto(
                    id = current.id,
                    nombre = current.nombre,
                    precioBase = price ?: 0.0,
                    img = current.img,
                    activo = current.activo
                )
            )
            _uiState.value = when (result) {
                is ProductoSaveResult.Success -> current.copy(
                    id = result.producto.id,
                    nombre = result.producto.nombre,
                    precioBase = result.producto.precioBase.toString(),
                    isSaving = false,
                    isSaved = true,
                    isDirty = false
                )
                ProductoSaveResult.DuplicateName -> current.copy(
                    isSaving = false,
                    validationMessage = "Ya existe un producto con ese nombre."
                )
                ProductoSaveResult.InvalidProduct -> current.copy(
                    isSaving = false,
                    validationMessage = "Captura un producto valido."
                )
                ProductoSaveResult.ProductNotFound -> current.copy(
                    isSaving = false,
                    errorMessage = "El producto no existe."
                )
                is ProductoSaveResult.PersistenceError -> current.copy(
                    isSaving = false,
                    errorMessage = "No se pudo guardar el producto."
                )
            }
        }
    }
}

private fun ProductoValidationResult.toMessage(): String = when (this) {
    ProductoValidationResult.Valid -> ""
    ProductoValidationResult.MissingName -> "El nombre del producto es obligatorio."
    ProductoValidationResult.NameTooLong -> "El nombre no debe exceder 120 caracteres."
    ProductoValidationResult.MissingOrInvalidPrice -> "El precio base debe ser mayor a cero."
    ProductoValidationResult.InvalidImageFormat -> "La imagen debe ser JPG o PNG."
    ProductoValidationResult.ImageTooLarge -> "La imagen no debe exceder 2 MB."
}
