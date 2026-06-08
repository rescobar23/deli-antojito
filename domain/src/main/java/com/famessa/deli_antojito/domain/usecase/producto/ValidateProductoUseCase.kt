package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.model.ProductoValidationResult

class ValidateProductoUseCase {
    operator fun invoke(
        nombre: String,
        precioBase: Double?,
        imageMimeType: String? = null,
        imageSizeBytes: Long? = null
    ): ProductoValidationResult {
        val trimmedName = nombre.trim()
        return when {
            trimmedName.isBlank() -> ProductoValidationResult.MissingName
            trimmedName.length > MAX_NAME_LENGTH -> ProductoValidationResult.NameTooLong
            precioBase == null || precioBase <= 0.0 -> ProductoValidationResult.MissingOrInvalidPrice
            imageMimeType != null && imageMimeType !in SUPPORTED_IMAGE_TYPES -> ProductoValidationResult.InvalidImageFormat
            imageSizeBytes != null && imageSizeBytes > MAX_IMAGE_BYTES -> ProductoValidationResult.ImageTooLarge
            else -> ProductoValidationResult.Valid
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 120
        const val MAX_IMAGE_BYTES = 2L * 1024L * 1024L
        val SUPPORTED_IMAGE_TYPES = setOf("image/jpeg", "image/jpg", "image/png")
    }
}
