package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.model.ProductoValidationResult
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidateProductoUseCaseTest {
    private val useCase = ValidateProductoUseCase()

    @Test
    fun validProductPassesValidation() {
        assertEquals(
            ProductoValidationResult.Valid,
            useCase(nombre = "Alitas", precioBase = 120.0, imageMimeType = "image/png", imageSizeBytes = 1024)
        )
    }

    @Test
    fun blankNameFailsValidation() {
        assertEquals(ProductoValidationResult.MissingName, useCase(nombre = " ", precioBase = 1.0))
    }

    @Test
    fun longNameFailsValidation() {
        assertEquals(ProductoValidationResult.NameTooLong, useCase(nombre = "A".repeat(121), precioBase = 1.0))
    }

    @Test
    fun nonPositivePriceFailsValidation() {
        assertEquals(ProductoValidationResult.MissingOrInvalidPrice, useCase(nombre = "Alitas", precioBase = 0.0))
    }

    @Test
    fun unsupportedImageTypeFailsValidation() {
        assertEquals(
            ProductoValidationResult.InvalidImageFormat,
            useCase(nombre = "Alitas", precioBase = 1.0, imageMimeType = "image/gif", imageSizeBytes = 1)
        )
    }

    @Test
    fun oversizedImageFailsValidation() {
        assertEquals(
            ProductoValidationResult.ImageTooLarge,
            useCase(nombre = "Alitas", precioBase = 1.0, imageMimeType = "image/png", imageSizeBytes = ValidateProductoUseCase.MAX_IMAGE_BYTES + 1)
        )
    }
}
