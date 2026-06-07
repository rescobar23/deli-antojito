package com.famessa.deli_antojito.data.Repository

import com.famessa.deli_antojito.data.entities.Configuracion
import com.famessa.deli_antojito.data.repository.ConfiguracionRepositoryImpl
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfiguracionRepositoryImplUpdateTest {
    @Test
    fun updatePreservesCreatedAtAndChangesUpdatedAt() = runTest {
        val repository = ConfiguracionRepositoryImpl(
            FakeConfiguracionDao(
                listOf(Configuracion(id = 1, nombreNegocio = "Antes", createdAt = 10L, updatedAt = 10L))
            )
        )

        val result = repository.updateConfiguracion("Despues")

        assertTrue(result is ConfiguracionSaveResult.Success)
        val configuracion = (result as ConfiguracionSaveResult.Success).configuracion
        assertEquals(10L, configuracion.createdAt)
        assertTrue(configuracion.updatedAt >= configuracion.createdAt)
    }

    @Test
    fun updateWithoutExistingRowIsInvalidLocalData() = runTest {
        val repository = ConfiguracionRepositoryImpl(FakeConfiguracionDao())

        assertEquals(ConfiguracionSaveResult.InvalidLocalData, repository.updateConfiguracion("Deli"))
    }

    @Test
    fun updateWithDuplicatesIsInvalidLocalData() = runTest {
        val repository = ConfiguracionRepositoryImpl(
            FakeConfiguracionDao(
                listOf(
                    Configuracion(id = 1, nombreNegocio = "Uno", createdAt = 1L, updatedAt = 1L),
                    Configuracion(id = 2, nombreNegocio = "Dos", createdAt = 1L, updatedAt = 1L)
                )
            )
        )

        assertEquals(ConfiguracionSaveResult.InvalidLocalData, repository.updateConfiguracion("Deli"))
    }
}
