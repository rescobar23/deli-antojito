package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes the currently saved business configuration, or null when none exists.
 */
class ObserveConfiguracionUseCase(
    private val repository: ConfiguracionRepository
) {
    operator fun invoke(): Flow<Configuracion?> = repository.observeConfiguracion()
}
