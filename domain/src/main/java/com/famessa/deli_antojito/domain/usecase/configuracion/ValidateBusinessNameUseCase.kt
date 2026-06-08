package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.BusinessNameValidationResult
import com.famessa.deli_antojito.domain.model.Configuracion

/**
 * Validates and normalizes business names before persistence.
 */
class ValidateBusinessNameUseCase {
    operator fun invoke(name: String): BusinessNameValidationResult {
        val trimmed = name.trim()
        return when {
            trimmed.isBlank() -> BusinessNameValidationResult.Blank
            trimmed.length > Configuracion.MAX_NOMBRE_NEGOCIO_LENGTH -> BusinessNameValidationResult.TooLong
            else -> BusinessNameValidationResult.Valid(trimmed)
        }
    }
}
