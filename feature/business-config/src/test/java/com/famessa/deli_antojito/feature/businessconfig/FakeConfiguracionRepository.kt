package com.famessa.deli_antojito.feature.businessconfig

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeConfiguracionRepository(
    initialConfiguration: Configuracion? = null
) : ConfiguracionRepository {
    private val configuracion = MutableStateFlow(initialConfiguration)
    var saveResult: ConfiguracionSaveResult = ConfiguracionSaveResult.Success(
        Configuracion(nombreNegocio = "Deli", createdAt = 1L, updatedAt = 1L)
    )
    var updateResult: ConfiguracionSaveResult = saveResult

    override fun observeConfiguracionState(): Flow<ConfiguracionState> {
        return MutableStateFlow(
            configuracion.value?.let { ConfiguracionState.Configured(it) }
                ?: ConfiguracionState.MissingConfiguration
        )
    }

    override fun observeConfiguracion(): Flow<Configuracion?> = configuracion

    override suspend fun saveConfiguracion(nombreNegocio: String): ConfiguracionSaveResult = saveResult

    override suspend fun updateConfiguracion(nombreNegocio: String): ConfiguracionSaveResult = updateResult
}
