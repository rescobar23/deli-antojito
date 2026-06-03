package com.famessa.deli_antojito.domain.repository

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import kotlinx.coroutines.flow.Flow

/**
 * Repository boundary for reading and persisting the single local business configuration.
 */
interface ConfiguracionRepository {
    fun observeConfiguracionState(): Flow<ConfiguracionState>
    fun observeConfiguracion(): Flow<Configuracion?>
    suspend fun saveConfiguracion(nombreNegocio: String): ConfiguracionSaveResult
    suspend fun updateConfiguracion(nombreNegocio: String): ConfiguracionSaveResult
}
