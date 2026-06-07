package com.famessa.deli_antojito.data.Repository

import com.famessa.deli_antojito.data.entities.Configuracion
import com.famessa.deli_antojito.data.repository.ConfiguracionRepositoryImpl
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfiguracionRepositoryImplExistingTest {
    @Test
    fun existingConfigurationCanBeObserved() = runTest {
        val repository = ConfiguracionRepositoryImpl(
            FakeConfiguracionDao(
                listOf(Configuracion(id = 1, nombreNegocio = "Deli Existente", createdAt = 1L, updatedAt = 1L))
            )
        )

        val state = repository.observeConfiguracionState().first()

        assertTrue(state is ConfiguracionState.Configured)
        assertEquals("Deli Existente", (state as ConfiguracionState.Configured).configuracion.nombreNegocio)
        assertEquals("Deli Existente", repository.observeConfiguracion().first()?.nombreNegocio)
    }
}
