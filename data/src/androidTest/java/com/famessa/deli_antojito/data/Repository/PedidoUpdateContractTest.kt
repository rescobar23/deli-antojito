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
class PedidoUpdateContractTest {
    @Test fun rejectsForeignLinesAndOnlyOneConcurrentEditOrTransitionCommits() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val command = fixture.newCommand()
            val first = repository.crear(command).order()
            val other = repository.crear(command.copy(token = "other")).order()
            val invalid = repository.actualizar(ActualizarPedidoCommand(first.id, first.revision,
                PedidoDraft("Cliente", listOf(PedidoDraftLinea.Existente(other.lineas.single().id, 1)))))
            assertTrue(invalid is PedidoWriteResult.ValidationError)
            val results = listOf(
                async { repository.actualizar(ActualizarPedidoCommand(first.id, first.revision,
                    PedidoDraft("Cambio", listOf(PedidoDraftLinea.Existente(first.lineas.single().id, 3))))) },
                async { repository.cambiarEstado(CambiarEstadoPedidoCommand(first.id, first.revision, EstadoPedido.EN_PREPARACION)) }
            ).awaitAll()
            assertEquals(1, results.count { it is PedidoWriteResult.Success })
            assertEquals(1, results.count { it == PedidoWriteResult.Conflict })
        }
    }
    @Test fun historicalPriceSurvivesCatalogDeletionAndConflictingEditIsRejected() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val original = repository.crear(fixture.newCommand()).order()
            val line = original.lineas.single()
            fixture.database.productoDao().deleteProducto(line.productoId)
            val command = ActualizarPedidoCommand(original.id, original.revision,
                PedidoDraft("Nuevo cliente", listOf(PedidoDraftLinea.Existente(line.id, 3))))
            val updated = repository.actualizar(command).order()
            assertEquals(3750L, updated.total.centavos)
            assertEquals("Fixture", updated.lineas.single().nombreProducto)
            assertEquals(original.numero, updated.numero)
            assertEquals(original.fechaCreacion, updated.fechaCreacion)
            assertEquals(PedidoWriteResult.Conflict, repository.actualizar(command))
        }
    }
    @Test fun failedReplacementPreservesWholePreviousVersion() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val original = repository.crear(fixture.newCommand()).order()
            fixture.failLineInsert()
            val result = repository.actualizar(ActualizarPedidoCommand(original.id, original.revision,
                PedidoDraft("Cambio", listOf(PedidoDraftLinea.Existente(original.lineas.single().id, 3)))))
            assertEquals(PedidoWriteResult.PersistenceError, result)
            val stored = (repository.observePedido(original.id).first() as PedidoReadResult.Success).value
            assertEquals(original, stored)
        }
    }
}

