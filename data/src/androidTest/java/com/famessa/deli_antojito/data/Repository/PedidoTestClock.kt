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

internal class PedidoTestClock : BusinessDayProvider {
    var now: Long = 1_000L
    private val calculator = LocalDayCalculator { TimeZone.getTimeZone("UTC") }
    override fun nowMillis() = now
    override fun observeDay() = flowOf(calculator.day(now))
    override fun range(from: LocalDateParts?, through: LocalDateParts?) = calculator.range(from, through)
}
internal suspend fun PedidoDatabaseFixture.newCommand(token: String = "token"): CrearPedidoCommand {
    val id = database.productoDao().insertProducto(Producto(nombre = "Fixture", precioBase = 12.50))
    return CrearPedidoCommand(token, PedidoDraft("María", listOf(
        PedidoDraftLinea.Nueva(PedidoProductSnapshot(id, "Fixture", Money(1250)), 2)
    )))
}
internal fun PedidoWriteResult.order(): Pedido = (this as PedidoWriteResult.Success).pedido

