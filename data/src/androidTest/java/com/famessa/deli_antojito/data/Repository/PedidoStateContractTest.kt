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
class PedidoStateContractTest {
    @Test fun persistsAuthorizedHistoryAndRejectsTerminalReopening() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val clock = PedidoTestClock()
            val repository = PedidoRepositoryImpl(fixture.database, clock)
            val pending = repository.crear(fixture.newCommand()).order()
            clock.now = 2000
            val preparing = repository.cambiarEstado(CambiarEstadoPedidoCommand(pending.id, pending.revision, EstadoPedido.EN_PREPARACION)).order()
            clock.now = 3000
            val completed = repository.cambiarEstado(CambiarEstadoPedidoCommand(preparing.id, preparing.revision, EstadoPedido.COMPLETADO)).order()
            assertEquals(PedidoWriteResult.InvalidTransition, repository.cambiarEstado(CambiarEstadoPedidoCommand(completed.id, completed.revision, EstadoPedido.PENDIENTE)))
            assertEquals(listOf(2000L, 3000L), fixture.database.pedidoDao().getTransitions(pending.id).map { it.fechaHora })
            assertEquals(PedidoWriteResult.Conflict, repository.actualizar(ActualizarPedidoCommand(pending.id, pending.revision, PedidoDraft("Cambio", listOf(PedidoDraftLinea.Existente(pending.lineas.single().id, 2))))))
        }
    }
    @Test fun failedHistoryInsertRollsBackState() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val order = repository.crear(fixture.newCommand()).order()
            fixture.failTransitionInsert()
            assertEquals(PedidoWriteResult.PersistenceError, repository.cambiarEstado(CambiarEstadoPedidoCommand(order.id, order.revision, EstadoPedido.CANCELADO)))
            assertEquals("PENDIENTE", fixture.database.pedidoDao().get(order.id)?.estado)
            assertEquals(emptyList<Any>(), fixture.database.pedidoDao().getTransitions(order.id))
        }
    }
}

