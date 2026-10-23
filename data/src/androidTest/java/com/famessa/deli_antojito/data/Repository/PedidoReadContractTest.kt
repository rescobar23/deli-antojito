package com.famessa.deli_antojito.data.Repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.famessa.deli_antojito.data.fixtures.PedidoDatabaseFixture
import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import com.famessa.deli_antojito.data.repository.PedidoRepositoryImpl
import com.famessa.deli_antojito.domain.model.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedidoReadContractTest {
    @Test fun missingAndCorruptOrdersAreExplicitReadOutcomes() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val repository = PedidoRepositoryImpl(fixture.database, PedidoTestClock())
            assertEquals(PedidoReadResult.NotFound, repository.observePedido(999).first())
            val order = repository.crear(fixture.newCommand()).order()
            fixture.database.openHelper.writableDatabase.execSQL("UPDATE pedidos SET estado='UNKNOWN' WHERE id=?", arrayOf(order.id))
            assertEquals(PedidoReadResult.InvalidLocalData, repository.observePedido(order.id).first())
            assertEquals(PedidoReadResult.InvalidLocalData, repository.observePedidos(PedidoConsulta()).first())
        }
    }
    @Test fun ordersByCreationThenIdAndKeepsDetail() = runTest {
        PedidoDatabaseFixture().use { fixture ->
            val dao = fixture.database.pedidoDao()
            for (number in 1L..3L) {
                val id = dao.insert(Pedido(numero = number, createToken = "t$number", cliente = "Cliente", clienteBusqueda = "cliente", numeroBusqueda = number.toString().padStart(5, '0'), fechaCreacion = if (number == 1L) 0 else 10, fechaActualizacion = 10, estado = "PENDIENTE", totalCentavos = 100, observaciones = null, revision = 1))
                dao.insertLines(listOf(PedidoDetalle(pedidoId = id, productoId = 999, nombreProducto = "Histórico", cantidad = 1, precioUnitarioCentavos = 100, subtotalCentavos = 100, posicion = 0)))
            }
            assertEquals(listOf(3L, 2L, 1L), dao.observeHeaders().first().map { it.numero })
            assertEquals("Histórico", dao.getLines(1).single().nombreProducto)
            assertEquals(3, dao.observeCount().first())
        }
    }
}
