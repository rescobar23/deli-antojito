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
class PedidoDailySummaryContractTest {
    @Test fun dailyFixtureMatchesFiveTwoOneOneAndExcludesYesterday() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val clock = PedidoTestClock()
            val repository = PedidoRepositoryImpl(fixture.database, clock)
            val command = fixture.newCommand()
            repeat(5) { index ->
                val order = repository.crear(command.copy(token = "today-$index")).order()
                if (index == 2 || index == 3) {
                    val preparing = repository.cambiarEstado(CambiarEstadoPedidoCommand(order.id, order.revision, EstadoPedido.EN_PREPARACION)).order()
                    if (index == 3) repository.cambiarEstado(CambiarEstadoPedidoCommand(order.id, preparing.revision, EstadoPedido.COMPLETADO))
                } else if (index == 4) repository.cambiarEstado(CambiarEstadoPedidoCommand(order.id, order.revision, EstadoPedido.CANCELADO))
            }
            clock.now = -1000
            val previous = repository.crear(command.copy(token = "yesterday")).order()
            clock.now = 2000
            repository.cambiarEstado(CambiarEstadoPedidoCommand(previous.id, previous.revision, EstadoPedido.CANCELADO))
            assertEquals(ResumenConteos(5, 2, 1, 1), (repository.observeResumen(0, 86400000).first() as PedidoReadResult.Success).value)
        }
    }
    @Test fun includesCancelledInTotalAndUsesCreationInsteadOfTransitionTime() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val clock = PedidoTestClock()
            val repository = PedidoRepositoryImpl(fixture.database, clock)
            val order = repository.crear(fixture.newCommand()).order()
            repository.cambiarEstado(CambiarEstadoPedidoCommand(order.id, order.revision, EstadoPedido.CANCELADO))
            val today = (repository.observeResumen(0, 86400000).first() as PedidoReadResult.Success).value
            assertEquals(ResumenConteos(1, 0, 0, 0), today)
            val tomorrow = (repository.observeResumen(86400000, 172800000).first() as PedidoReadResult.Success).value
            assertEquals(ResumenConteos(0, 0, 0, 0), tomorrow)
        }
    }
}

