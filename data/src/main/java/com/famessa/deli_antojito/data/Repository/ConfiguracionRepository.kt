package com.famessa.deli_antojito.data.repository

import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Configuracion
import kotlinx.coroutines.flow.Flow

interface IConfiguracionRepository {
    suspend fun insertConfiguracion(configuracion: Configuracion)
    fun getConfiguracion(): Flow<Configuracion>
}

class ConfiguracionRepository(private val database: AppDatabase) : IConfiguracionRepository {
    private val configuracionDao = database.configuracionDao()

    override suspend fun insertConfiguracion(configuracion: Configuracion) {
        configuracionDao.insertConfiguracion(configuracion)
    }

    override fun getConfiguracion(): Flow<Configuracion> {
        return configuracionDao.getConfiguracion()
    }
}