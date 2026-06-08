package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetStartupConfigurationStateUseCaseExistingTest {
    @Test
    fun existingConfigurationIsStartupConfiguredState() = runTest {
        val repository = FakeConfiguracionRepository(
            ConfiguracionState.Configured(Configuracion(nombreNegocio = "Deli Local"))
        )
        val useCase = GetStartupConfigurationStateUseCase(repository)

        val state = useCase().drop(1).first()

        assertTrue(state is ConfiguracionState.Configured)
        assertEquals("Deli Local", (state as ConfiguracionState.Configured).configuracion.nombreNegocio)
    }
}
