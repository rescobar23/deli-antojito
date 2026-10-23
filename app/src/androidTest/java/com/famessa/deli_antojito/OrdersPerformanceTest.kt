package com.famessa.deli_antojito

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import com.famessa.deli_antojito.data.repository.PedidoRepositoryImpl
import com.famessa.deli_antojito.domain.model.normalizePedidoSearch
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.usecase.pedido.ObservePedidosUseCase
import com.famessa.deli_antojito.feature.orders.list.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test

class OrdersPerformanceTest {
    @get:Rule val compose = createComposeRule()

    @Test fun thousandOrdersMeetReadAndSearchBudgetsInRelease() {
        assumeFalse("Run with -PordersPerformance=true on the baseline device", BuildConfig.DEBUG)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addCallback(AppDatabase.CREATE_CALLBACK).build()
        val store = ViewModelStore()
        try {
            runBlocking { database.withTransaction {
                for (number in 1L..1000L) {
                    val client = "Cliente fixture ${number.toString().padStart(5, '0')}"
                    val id = database.pedidoDao().insert(Pedido(numero = number, createToken = "fixture-$number", cliente = client,
                        clienteBusqueda = normalizePedidoSearch(client), numeroBusqueda = number.toString().padStart(5, '0'),
                        fechaCreacion = number * 1000, fechaActualizacion = number * 1000, estado = "PENDIENTE",
                        totalCentavos = 100, observaciones = null, revision = 1))
                    database.pedidoDao().insertLines(listOf(PedidoDetalle(pedidoId = id, productoId = 1, nombreProducto = "Fixture", cantidad = 1,
                        precioUnitarioCentavos = 100, subtotalCentavos = 100, posicion = 0)))
                }
            } }
            val clock = object : BusinessDayProvider {
                override fun nowMillis() = 0L
                override fun observeDay() = flowOf(BusinessDay(LocalDateParts(2026,10,21),0,86400000))
                override fun range(from: LocalDateParts?, through: LocalDateParts?) = EpochRange(null, null)
            }
            val repository = PedidoRepositoryImpl(database, clock)
            val model = ViewModelProvider(store, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    requireNotNull(modelClass.cast(OrdersViewModel(ObservePedidosUseCase(repository))))
            })[OrdersViewModel::class.java]
            compose.setContent { val state by model.uiState.collectAsState(); OrdersScreen(state) }
            compose.waitUntil(10_000) { !model.uiState.value.loading && model.uiState.value.pedidos.size == 1000 }
            val openings = mutableListOf<Double>()
            val searches = mutableListOf<Double>()
            repeat(35) { iteration ->
                compose.runOnIdle { model.search("#00001") }
                compose.waitUntil(10_000) { !model.uiState.value.loading && model.uiState.value.pedidos.singleOrNull()?.numero == 1L }
                val start = SystemClock.elapsedRealtimeNanos()
                compose.runOnIdle { model.search("") }
                compose.waitUntil(10_000) { !model.uiState.value.loading && model.uiState.value.pedidos.size == 1000 }
                compose.waitForIdle()
                val openedMs = (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000.0
                val number = if (iteration % 2 == 0) 25L else 26L
                val searchStart = SystemClock.elapsedRealtimeNanos()
                compose.runOnIdle { model.search("#${number.toString().padStart(5, '0')}") }
                compose.waitUntil(10_000) { !model.uiState.value.loading && model.uiState.value.pedidos.singleOrNull()?.numero == number }
                compose.waitForIdle()
                val searchMs = (SystemClock.elapsedRealtimeNanos() - searchStart) / 1_000_000.0
                if (iteration >= 5) { openings += openedMs; searches += searchMs }
            }
            val p95 = searches.sorted()[kotlin.math.ceil(searches.size * .95).toInt() - 1]
            InstrumentationRegistry.getInstrumentation().sendStatus(0, Bundle().apply {
                putString("orders_performance", "openings_ms=$openings; searches_ms=$searches; p95_ms=$p95")
            })
            assertTrue("List read/render exceeded 2 seconds: $openings", openings.all { it < 2000 })
            assertTrue("Search p95 exceeded 1 second: $p95", p95 < 1000)
        } finally { store.clear(); database.close() }
    }
}
