package com.famessa.deli_antojito.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.famessa.deli_antojito.data.entities.Configuracion
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfiguracionDao {
    @Insert
    suspend fun insertConfiguracion(configuracion: Configuracion)

    @Query("SELECT * FROM configuracion LIMIT 1")
    fun getConfiguracion(): Flow<Configuracion>
}