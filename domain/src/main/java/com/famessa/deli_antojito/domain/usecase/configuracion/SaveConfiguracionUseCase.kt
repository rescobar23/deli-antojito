package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.BusinessNameValidationResult
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository

/**
 * Saves or updates the single business configuration after applying domain validation.
 */
class SaveConfiguracionUseCase(
    private val repository: ConfiguracionRepository,
    private val validateBusinessName: ValidateBusinessNameUseCase
) {
    suspend fun saveInitial(nombreNegocio: String): ConfiguracionSaveResult {
        return when (val validation = validateBusinessName(nombreNegocio)) {
            BusinessNameValidationResult.Blank,
            BusinessNameValidationResult.TooLong -> ConfiguracionSaveResult.InvalidName
            is BusinessNameValidationResult.Valid -> repository.saveConfiguracion(validation.trimmedName)
        }
    }

    suspend fun update(nombreNegocio: String): ConfiguracionSaveResult {
        return when (val validation = validateBusinessName(nombreNegocio)) {
            BusinessNameValidationResult.Blank,
            BusinessNameValidationResult.TooLong -> ConfiguracionSaveResult.InvalidName
            is BusinessNameValidationResult.Valid -> repository.updateConfiguracion(validation.trimmedName)
        }
    }
}
