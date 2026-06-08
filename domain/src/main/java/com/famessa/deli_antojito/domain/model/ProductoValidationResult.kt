package com.famessa.deli_antojito.domain.model

sealed interface ProductoValidationResult {
    data object Valid : ProductoValidationResult
    data object MissingName : ProductoValidationResult
    data object NameTooLong : ProductoValidationResult
    data object MissingOrInvalidPrice : ProductoValidationResult
    data object InvalidImageFormat : ProductoValidationResult
    data object ImageTooLarge : ProductoValidationResult
}
