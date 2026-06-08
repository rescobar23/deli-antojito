package com.famessa.deli_antojito.domain.model

/**
 * Startup and persistence state for the local business configuration.
 */
sealed interface ConfiguracionState {
    data object Loading : ConfiguracionState
    data object MissingConfiguration : ConfiguracionState
    data class Configured(val configuracion: Configuracion) : ConfiguracionState
    data object InvalidLocalData : ConfiguracionState
    data class PersistenceError(val cause: Throwable? = null) : ConfiguracionState
}

/**
 * Result of saving or updating the local business configuration.
 */
sealed interface ConfiguracionSaveResult {
    data class Success(val configuracion: Configuracion) : ConfiguracionSaveResult
    data object InvalidName : ConfiguracionSaveResult
    data object InvalidLocalData : ConfiguracionSaveResult
    data class PersistenceError(val cause: Throwable? = null) : ConfiguracionSaveResult
}

/**
 * Result of validating user-entered business names.
 */
sealed interface BusinessNameValidationResult {
    data class Valid(val trimmedName: String) : BusinessNameValidationResult
    data object Blank : BusinessNameValidationResult
    data object TooLong : BusinessNameValidationResult
}
