package com.famessa.deli_antojito.data.Repository

import com.famessa.deli_antojito.data.dao.ConfiguracionDao
import com.famessa.deli_antojito.data.entities.Configuracion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeConfiguracionDao(
    initialRows: List<Configuracion> = emptyList(),
    private val failReads: Boolean = false,
    private val failWrites: Boolean = false
) : ConfiguracionDao {
    private val rows = MutableStateFlow(initialRows)

    override suspend fun upsertConfiguracion(configuracion: Configuracion) {
        if (failWrites) throw IllegalStateException("write failed")
        rows.value = rows.value.filterNot { it.id == configuracion.id } + configuracion
    }

    override fun observeConfiguraciones(): Flow<List<Configuracion>> {
        return if (failReads) flow { throw IllegalStateException("read failed") } else rows
    }

    override suspend fun getConfiguraciones(): List<Configuracion> {
        if (failReads) throw IllegalStateException("read failed")
        return rows.value
    }

    override suspend fun countConfiguraciones(): Int {
        if (failReads) throw IllegalStateException("read failed")
        return rows.value.size
    }
}
