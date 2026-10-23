package com.famessa.deli_antojito.data.cierre
import com.famessa.deli_antojito.data.repository.CierreConsultaRepositoryImpl
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.usecase.cierre.CalculateConsultaCierreUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.*
import androidx.room.withTransaction
import org.junit.Test
import org.junit.Assert.*
class CierreHistoricalProductsTest {
    @Test fun snapshotDoesNotNeedCatalogAndStateRefreshUsesCreation() = runTest {
        CierreRoomFixtures().use { fixture ->
            fixture.database.productoDao().insertProducto(com.famessa.deli_antojito.data.entities.Producto(id=7,nombre="Actual",precioBase=99.99))
            fixture.insert(1000); val pending=fixture.insert(1500,"PENDIENTE")
            val repository=CierreConsultaRepositoryImpl(fixture.database.pedidoDao())
            val period=CierrePeriodo(1000,2000,"UTC")
            suspend fun query() = (CalculateConsultaCierreUseCase()(period,(repository.readCompleted(period) as ConsultaSnapshotsResult.Success).pedidos) as ConsultaCierreResult.Success).consulta
            val original=query(); assertEquals("Histórico",original.topProductos.single().nombreHistorico)
            fixture.database.openHelper.writableDatabase.execSQL("UPDATE productos SET nombre='Otro nombre', precio_base=0.01, activo=0")
            assertEquals("Otro nombre",fixture.database.productoDao().getProductoById(7)?.nombre)
            assertEquals(original,query())
            fixture.database.openHelper.writableDatabase.execSQL("DELETE FROM productos")
            assertNull(fixture.database.productoDao().getProductoById(7))
            assertEquals(original,query())
            fixture.database.pedidoDao().transition(pending,0,"PENDIENTE","COMPLETADO",9000)
            val updated=query(); assertEquals(2,updated.resumen.totalPedidos); assertEquals(4L,updated.topProductos.single().cantidad)
        }
    }
    @Test fun concurrentChangesNeverMixSummaryAndProducts() = runTest {
        CierreRoomFixtures().use { fixture ->
            fixture.insert(1000); val changing=fixture.insert(1500,"PENDIENTE")
            val reader=CierreConsultaRepositoryImpl(fixture.database.pedidoDao())
            val period=CierrePeriodo(1000,2000,"UTC")
            coroutineScope {
                val writer=launch(Dispatchers.IO) { repeat(30) { iteration ->
                    fixture.database.withTransaction {fixture.database.openHelper.writableDatabase.execSQL("UPDATE pedidos SET estado=? WHERE id=?",arrayOf(if(iteration%2==0) "COMPLETADO" else "PENDIENTE",changing))}
                } }
                repeat(30) {
                    val snapshots=reader.readCompleted(period) as ConsultaSnapshotsResult.Success
                    val query=(CalculateConsultaCierreUseCase()(period,snapshots.pedidos) as ConsultaCierreResult.Success).consulta
                    assertEquals(query.resumen.totalPedidos*2L,query.topProductos.single().cantidad)
                    assertEquals(query.resumen.totalPedidos*200L,query.resumen.bruto.centavos)
                }
                writer.join()
            }
        }
    }    @Test fun historicalDataSurvivesDatabaseReopen() = runTest {
        val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val name="cierre-test-${java.util.UUID.randomUUID()}.db"
        try {
            val period=CierrePeriodo(1000,2000,"UTC")
            lateinit var original: ConsultaCierre
            CierreRoomFixtures(name).use { fixture ->
                fixture.insert(1000)
                val snapshots=CierreConsultaRepositoryImpl(fixture.database.pedidoDao()).readCompleted(period) as ConsultaSnapshotsResult.Success
                original=(CalculateConsultaCierreUseCase()(period,snapshots.pedidos) as ConsultaCierreResult.Success).consulta
            }
            CierreRoomFixtures(name).use { fixture ->
                val snapshots=CierreConsultaRepositoryImpl(fixture.database.pedidoDao()).readCompleted(period) as ConsultaSnapshotsResult.Success
                assertEquals(original,(CalculateConsultaCierreUseCase()(period,snapshots.pedidos) as ConsultaCierreResult.Success).consulta)
            }
        } finally {context.deleteDatabase(name)}
    }}




