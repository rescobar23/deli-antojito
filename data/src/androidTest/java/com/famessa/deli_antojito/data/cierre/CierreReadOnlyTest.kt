package com.famessa.deli_antojito.data.cierre
import com.famessa.deli_antojito.data.repository.CierreConsultaRepositoryImpl
import com.famessa.deli_antojito.domain.model.cierre.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
class CierreReadOnlyTest {
    @Test fun queryingCannotMutateAnyTable() = runTest {
        CierreRoomFixtures().use { fixture ->
            fixture.insert(1000)
            fun snapshot(): List<List<String>> {
                val db=fixture.database.openHelper.readableDatabase
                return listOf("pedidos","pedido_detalle","pedido_transiciones","pedido_secuencia","productos","configuracion").map { table ->
                    db.query("SELECT * FROM $table ORDER BY rowid").use { cursor -> buildList { while(cursor.moveToNext()) add((0 until cursor.columnCount).joinToString("|") { cursor.getString(it) ?: "NULL" }) } }
                }
            }
            val before=snapshot()
            repeat(3) { CierreConsultaRepositoryImpl(fixture.database.pedidoDao()).readCompleted(CierrePeriodo(0,2000,"UTC")) }
            assertEquals(before,snapshot()); assertEquals(4,fixture.database.openHelper.readableDatabase.version)
        }
    }
}
