package com.famessa.deli_antojito.domain.repository

import com.famessa.deli_antojito.domain.model.Configuracion
import kotlinx.coroutines.flow.Flow

interface ConfiguracionRepository {
    suspend fun insertConfiguracion(configuracion: Configuracion)
    fun getConfiguracion(): Flow<Configuracion>
}
