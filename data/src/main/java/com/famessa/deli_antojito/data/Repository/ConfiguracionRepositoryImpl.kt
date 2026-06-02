package com.famessa.deli_antojito.data.repository

import com.famessa.deli_antojito.data.dao.ConfiguracionDao
import com.famessa.deli_antojito.data.mappers.toDomain
import com.famessa.deli_antojito.data.mappers.toEntity
import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConfiguracionRepositoryImpl @Inject constructor(
    private val configuracionDao: ConfiguracionDao
) : ConfiguracionRepository {

    override suspend fun insertConfiguracion(configuracion: Configuracion) {
        configuracionDao.insertConfiguracion(configuracion.toEntity())
    }

    override fun getConfiguracion(): Flow<Configuracion> {
        return configuracionDao.getConfiguracion().map { it.toDomain() }
    }
}
