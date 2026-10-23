package com.famessa.deli_antojito
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.famessa.deli_antojito.core.ui.theme.Deli_antojitoTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.*
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import com.famessa.deli_antojito.data.repository.CierreConsultaRepositoryImpl
import com.famessa.deli_antojito.data.time.LocalCierreDateTimeResolver
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.model.cierre.CierrePeriodo
import com.famessa.deli_antojito.domain.usecase.cierre.*
import com.famessa.deli_antojito.feature.batchclosing.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.Assume.assumeFalse

class BatchClosingPerformanceTest {
    @get:Rule val compose=createComposeRule()
    @Test fun thousandOrdersWithTenLinesRenderWithinBudget() {
        assumeFalse("Run release with -PordersPerformance=true",BuildConfig.DEBUG)
        val context=ApplicationProvider.getApplicationContext<Context>()
        val database=Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).addCallback(AppDatabase.CREATE_CALLBACK).build()
        val store=ViewModelStore()
        try {
            runBlocking { database.withTransaction {
                for(number in 1L..1000L) {
                    val id=database.pedidoDao().insert(Pedido(numero=number,createToken="perf-$number",cliente="Sintético",clienteBusqueda="sintetico",numeroBusqueda=number.toString(),fechaCreacion=number*1000,fechaActualizacion=number*1000,estado="COMPLETADO",totalCentavos=1000,observaciones=null,revision=0))
                    database.pedidoDao().insertLines((0..9).map { position ->
                        val product=(number+position)%100+1
                        PedidoDetalle(pedidoId=id,productoId=product,nombreProducto="Producto$product",cantidad=1,precioUnitarioCentavos=100,subtotalCentavos=100,posicion=position)
                    })
                }
            } }
            val clock=object : BusinessDayProvider {
                override fun nowMillis()=2000000L
                override fun observeDay()=flowOf(BusinessDay(LocalDateParts(1970,1,1),0,86400000))
                override fun range(from: LocalDateParts?,through: LocalDateParts?)=EpochRange(null,null)
            }
            val resolver=LocalCierreDateTimeResolver()
            val model=ViewModelProvider(store,object : ViewModelProvider.Factory {
                override fun <T:ViewModel> create(modelClass:Class<T>):T = requireNotNull(modelClass.cast(BatchClosingViewModel(
                    GetConsultaCierreUseCase(CierreConsultaRepositoryImpl(database.pedidoDao()),clock::nowMillis,ValidateCierrePeriodoUseCase(resolver::isValidZone),CalculateConsultaCierreUseCase()),clock,resolver,SavedStateHandle())))
            })[BatchClosingViewModel::class.java]
            compose.setContent { val state by model.state.collectAsState(); CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density,1f)) { Deli_antojitoTheme(darkTheme=false) { BatchClosingScreen(state,{},model::beginEdit,model::refresh,model::cancelEdit,model::selectDate) } } }
            compose.waitUntil(10000) { model.state.value.status==BatchStatus.Ready }
            val samples=mutableListOf<Double>()
            repeat(35) { iteration ->
                val period=CierrePeriodo(0,1000001L+iteration,"UTC")
                val start=SystemClock.elapsedRealtimeNanos()
                compose.runOnIdle { model.selectPeriod(period) }
                compose.waitUntil(10000) { model.state.value.consulta?.periodo==period && model.state.value.status==BatchStatus.Ready }
                compose.waitForIdle()
                compose.onNodeWithTag("batch_order_count").performScrollTo().assertIsDisplayed().assertTextContains("1000",substring=true)
                // The adaptive screen scrolls vertically: visit both result viewports inside the timed budget.
                compose.onNodeWithTag("batch_top_product_5").performScrollTo().assertIsDisplayed()
                assertEquals(5,model.state.value.consulta?.topProductos?.size)
                val elapsed=(SystemClock.elapsedRealtimeNanos()-start)/1000000.0
                if(iteration>=5) samples+=elapsed
            }
            val p95=samples.sorted()[28]
            val metrics="samples_ms=$samples;max_ms=${samples.maxOrNull()};p95_ms=$p95;system_font_scale=${context.resources.configuration.fontScale};test_font_scale=1.0;model=${android.os.Build.MODEL};api=${android.os.Build.VERSION.SDK_INT}"
            println("BATCH_CLOSING_PERFORMANCE $metrics")
            InstrumentationRegistry.getInstrumentation().sendStatus(0,Bundle().apply {putString("batch_performance",metrics)})
            assertTrue("Read/render exceeded 2s: $samples",samples.all {it<2000})
            // A mixed set checks the same dataset's current-state selection, outside measured samples.
            runBlocking { database.withTransaction { database.openHelper.writableDatabase.execSQL("UPDATE pedidos SET estado=CASE WHEN numero%4=0 THEN 'PENDIENTE' WHEN numero%4=1 THEN 'EN_PREPARACION' WHEN numero%4=2 THEN 'CANCELADO' ELSE 'COMPLETADO' END") } }
            compose.runOnIdle {model.refresh()}
            compose.waitUntil(10000) {model.state.value.consulta?.resumen?.totalPedidos==250}
        } finally {store.clear();database.close()}
    }
}




