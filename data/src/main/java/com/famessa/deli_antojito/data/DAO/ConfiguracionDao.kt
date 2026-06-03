package com.famessa.deli_antojito.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.famessa.deli_antojito.data.entities.Configuracion
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfiguracionDao {
    @Upsert
    suspend fun upsertConfiguracion(configuracion: Configuracion)

    @Query("SELECT * FROM configuracion ORDER BY id")
    fun observeConfiguraciones(): Flow<List<Configuracion>>

    @Query("SELECT * FROM configuracion ORDER BY id")
    suspend fun getConfiguraciones(): List<Configuracion>

    @Query("SELECT COUNT(*) FROM configuracion")
    suspend fun countConfiguraciones(): Int
}
