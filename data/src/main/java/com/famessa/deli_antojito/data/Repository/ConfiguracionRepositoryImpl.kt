package com.famessa.deli_antojito.data.repository

import com.famessa.deli_antojito.data.dao.ConfiguracionDao
import com.famessa.deli_antojito.data.mappers.toDomain
import com.famessa.deli_antojito.data.mappers.toEntity
import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ConfiguracionRepositoryImpl @Inject constructor(
    private val configuracionDao: ConfiguracionDao
) : ConfiguracionRepository {

    override fun observeConfiguracionState(): Flow<ConfiguracionState> {
        return configuracionDao.observeConfiguraciones()
            .map { rows ->
                when (rows.size) {
                    0 -> ConfiguracionState.MissingConfiguration
                    1 -> ConfiguracionState.Configured(rows.first().toDomain())
                    else -> ConfiguracionState.InvalidLocalData
                }
            }
            .catch { error ->
                Timber.e(error, "Failed to read business configuration state")
                emit(ConfiguracionState.PersistenceError(error))
            }
    }

    override fun observeConfiguracion(): Flow<Configuracion?> {
        return configuracionDao.observeConfiguraciones()
            .map { rows -> rows.singleOrNull()?.toDomain() }
            .catch { error ->
                Timber.e(error, "Failed to observe business configuration")
                emit(null)
            }
    }

    override suspend fun saveConfiguracion(nombreNegocio: String): ConfiguracionSaveResult {
        return persist(nombreNegocio, allowMissing = true)
    }

    override suspend fun updateConfiguracion(nombreNegocio: String): ConfiguracionSaveResult {
        return persist(nombreNegocio, allowMissing = false)
    }

    private suspend fun persist(
        nombreNegocio: String,
        allowMissing: Boolean
    ): ConfiguracionSaveResult {
        return runCatching {
            val rows = configuracionDao.getConfiguraciones()
            if (rows.size > 1) return ConfiguracionSaveResult.InvalidLocalData
            if (!allowMissing && rows.isEmpty()) return ConfiguracionSaveResult.InvalidLocalData

            val now = System.currentTimeMillis()
            val current = rows.singleOrNull()?.toDomain()
            val configuracion = Configuracion(
                id = Configuracion.SINGLETON_ID,
                nombreNegocio = nombreNegocio,
                createdAt = current?.createdAt?.takeIf { it > 0L } ?: now,
                updatedAt = now
            )
            configuracionDao.upsertConfiguracion(configuracion.toEntity())
            ConfiguracionSaveResult.Success(configuracion)
        }.getOrElse { error ->
            Timber.e(error, "Failed to persist business configuration")
            ConfiguracionSaveResult.PersistenceError(error)
        }
    }
}
