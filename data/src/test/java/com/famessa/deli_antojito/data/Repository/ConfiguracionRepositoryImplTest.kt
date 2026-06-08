package com.famessa.deli_antojito.data.Repository

import com.famessa.deli_antojito.data.entities.Configuracion
import com.famessa.deli_antojito.data.repository.ConfiguracionRepositoryImpl
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfiguracionRepositoryImplTest {
    @Test
    fun emptyRowsEmitMissingState() = runTest {
        val repository = ConfiguracionRepositoryImpl(FakeConfiguracionDao())

        assertEquals(ConfiguracionState.MissingConfiguration, repository.observeConfiguracionState().first())
    }

    @Test
    fun saveSuccessPersistsSingletonConfiguration() = runTest {
        val repository = ConfiguracionRepositoryImpl(FakeConfiguracionDao())

        val result = repository.saveConfiguracion("Deli")

        assertTrue(result is ConfiguracionSaveResult.Success)
        assertEquals("Deli", (result as ConfiguracionSaveResult.Success).configuracion.nombreNegocio)
    }

    @Test
    fun duplicateRowsEmitInvalidLocalData() = runTest {
        val repository = ConfiguracionRepositoryImpl(
            FakeConfiguracionDao(
                listOf(
                    Configuracion(id = 1, nombreNegocio = "Uno", createdAt = 1L, updatedAt = 1L),
                    Configuracion(id = 2, nombreNegocio = "Dos", createdAt = 1L, updatedAt = 1L)
                )
            )
        )

        assertEquals(ConfiguracionState.InvalidLocalData, repository.observeConfiguracionState().first())
    }

    @Test
    fun saveFailureMapsToPersistenceError() = runTest {
        val repository = ConfiguracionRepositoryImpl(FakeConfiguracionDao(failWrites = true))

        assertTrue(repository.saveConfiguracion("Deli") is ConfiguracionSaveResult.PersistenceError)
    }
}
