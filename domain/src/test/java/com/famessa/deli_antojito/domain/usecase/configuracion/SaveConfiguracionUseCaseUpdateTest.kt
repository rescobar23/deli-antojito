package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveConfiguracionUseCaseUpdateTest {
    private val repository = FakeConfiguracionRepository()
    private val useCase = SaveConfiguracionUseCase(repository, ValidateBusinessNameUseCase())

    @Test
    fun updatesValidName() = runTest {
        repository.updateResult = ConfiguracionSaveResult.Success(
            Configuracion(nombreNegocio = "Nuevo Nombre", createdAt = 1L, updatedAt = 2L)
        )

        val result = useCase.update(" Nuevo Nombre ")

        assertTrue(result is ConfiguracionSaveResult.Success)
        assertEquals("Nuevo Nombre", (result as ConfiguracionSaveResult.Success).configuracion.nombreNegocio)
    }

    @Test
    fun rejectsInvalidUpdate() = runTest {
        assertEquals(ConfiguracionSaveResult.InvalidName, useCase.update(" "))
    }

    @Test
    fun returnsUpdateFailure() = runTest {
        repository.updateResult = ConfiguracionSaveResult.PersistenceError()

        assertTrue(useCase.update("Nuevo") is ConfiguracionSaveResult.PersistenceError)
    }
}
