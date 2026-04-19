package com.famessa.deli_antojito.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.famessa.deli_antojito.data.Entities

@Dao
interface ConfiguracionDao {
    @Insert
    suspend fun insertConfigracion(configuracion: Configuracion)

    @Query("SELECT TOP 1 * FROM confguracion")
    fun getConfiguracion(): Flow<Configuracion>

}