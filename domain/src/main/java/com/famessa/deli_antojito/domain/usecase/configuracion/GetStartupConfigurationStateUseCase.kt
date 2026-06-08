package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.ConfiguracionState
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

/**
 * Observes the state that decides whether startup shows setup, normal content, or an error.
 */
class GetStartupConfigurationStateUseCase(
    private val repository: ConfiguracionRepository
) {
    operator fun invoke(): Flow<ConfiguracionState> {
        return repository.observeConfiguracionState()
            .onStart { emit(ConfiguracionState.Loading) }
    }
}
