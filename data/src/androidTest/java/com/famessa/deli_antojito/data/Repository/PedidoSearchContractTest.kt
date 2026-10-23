package com.famessa.deli_antojito.data.Repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.famessa.deli_antojito.data.fixtures.PedidoDatabaseFixture
import com.famessa.deli_antojito.data.repository.PedidoRepositoryImpl
import com.famessa.deli_antojito.data.entities.Producto
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.data.time.LocalDayCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.TimeZone

@RunWith(AndroidJUnit4::class)
class PedidoSearchContractTest {
    @Test fun unicodeLiteralAndIntersectingFiltersAreConsistent() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            repository.crear(fixture.newCommand())
            fun PedidoReadResult<List<PedidoResumen>>.size() = (this as PedidoReadResult.Success).value.size
            assertEquals(1, repository.observePedidos(PedidoConsulta(texto = " MARI\u0301A ")).first().size())
            assertEquals(1, repository.observePedidos(PedidoConsulta(texto = "#00001")).first().size())
            assertEquals(0, repository.observePedidos(PedidoConsulta(texto = "%")).first().size())
            assertEquals(0, repository.observePedidos(PedidoConsulta(estadoPestana = EstadoPedido.PENDIENTE, estadosAvanzados = setOf(EstadoPedido.CANCELADO))).first().size())
            assertEquals(1, repository.observePedidos(PedidoConsulta(minimo = Money(2500), maximo = Money(2500))).first().size())
        }
    }
}

