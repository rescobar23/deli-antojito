package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeConfiguracionRepository(
    initialState: ConfiguracionState = ConfiguracionState.MissingConfiguration,
    private val failOnObserve: Boolean = false
) : ConfiguracionRepository {
    val state = MutableStateFlow(initialState)
    var saveResult: ConfiguracionSaveResult = ConfiguracionSaveResult.Success(
        Configuracion(nombreNegocio = "Demo", createdAt = 1L, updatedAt = 1L)
    )
    var updateResult: ConfiguracionSaveResult = saveResult

    override fun observeConfiguracionState(): Flow<ConfiguracionState> {
        return if (failOnObserve) flow { throw IllegalStateException("read failed") } else state
    }

    override fun observeConfiguracion(): Flow<Configuracion?> {
        return MutableStateFlow((state.value as? ConfiguracionState.Configured)?.configuracion)
    }

    override suspend fun saveConfiguracion(nombreNegocio: String): ConfiguracionSaveResult = saveResult

    override suspend fun updateConfiguracion(nombreNegocio: String): ConfiguracionSaveResult = updateResult
}
