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
class PedidoCreateContractTest {
    @Test fun sameProductAndPriceMergeButDistinctHistoricalPricesStaySeparate() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val command = fixture.newCommand()
            val line = command.draft.lineas.single() as PedidoDraftLinea.Nueva
            val order = repository.crear(command.copy(draft = command.draft.copy(lineas = listOf(
                line, line.copy(cantidad = 1), line.copy(snapshot = line.snapshot.copy(precio = Money(1500)), cantidad = 1)
            )))).order()
            assertEquals(2, order.lineas.size)
            assertEquals(listOf(3, 1), order.lineas.map { it.cantidad })
            assertEquals(5250L, order.total.centavos)
        }
    }
    @Test fun confirmedOrderAndTokenSurviveDatabaseReopen() = runTest {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val name = "pedidos-fixture-${java.util.UUID.randomUUID()}.db"
        try {
            lateinit var command: CrearPedidoCommand
            var orderId = 0L
            PedidoDatabaseFixture(name).use { fixture ->
                command = fixture.newCommand()
                orderId = PedidoRepositoryImpl(fixture.database, PedidoTestClock()).crear(command).order().id
            }
            PedidoDatabaseFixture(name).use { fixture ->
                val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
                assertEquals(orderId, repository.crear(command).order().id)
                assertEquals(2500L, (repository.observePedido(orderId).first() as PedidoReadResult.Success).value.total.centavos)
                assertEquals(1, fixture.database.pedidoDao().observeCount().first())
            }
        } finally { context.deleteDatabase(name) }
    }
    @Test fun distinctConcurrentOrdersAndExhaustedSequenceNeverRepeatNumbers() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val command = fixture.newCommand()
            val orders = listOf(async { repository.crear(command).order() }, async { repository.crear(command.copy(token = "second")).order() }).awaitAll()
            assertEquals(setOf(1L, 2L), orders.map { it.numero }.toSet())
            fixture.database.openHelper.writableDatabase.execSQL("UPDATE pedido_secuencia SET siguiente_numero=?", arrayOf(Long.MAX_VALUE))
            assertTrue(repository.crear(command.copy(token = "exhausted")) !is PedidoWriteResult.Success)
            assertEquals(2, fixture.database.pedidoDao().observeCount().first())
            assertEquals(Long.MAX_VALUE, fixture.database.pedidoDao().nextNumber())
        }
    }
    @Test fun repeatedAndConcurrentSubmissionsHaveOneIdentityPerToken() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val command = fixture.newCommand()
            val results = listOf(async { repository.crear(command) }, async { repository.crear(command) }).awaitAll()
            assertEquals(results[0].order().id, results[1].order().id)
            val other = repository.crear(command.copy(token = "other")).order()
            assertEquals(2, fixture.database.pedidoDao().observeCount().first())
            assertEquals(2L, other.numero)
        }
    }
    @Test fun failingLinesRollBackHeaderAndSequence() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val command = fixture.newCommand()
            fixture.failLineInsert()
            assertEquals(PedidoWriteResult.PersistenceError, repository.crear(command))
            assertEquals(0, fixture.database.pedidoDao().observeCount().first())
            assertEquals(1L, fixture.database.pedidoDao().nextNumber())
        }
    }
    @Test fun deletedNewProductIsUnavailable() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            val command = fixture.newCommand()
            val productId = (command.draft.lineas.single() as PedidoDraftLinea.Nueva).snapshot.productoId
            fixture.database.productoDao().deleteProducto(productId)
            assertTrue(repository.crear(command) is PedidoWriteResult.ProductUnavailable)
            assertEquals(0, fixture.database.pedidoDao().observeCount().first())
        }
    }
}

