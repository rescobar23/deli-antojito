package com.famessa.deli_antojito.data.mappers
import com.famessa.deli_antojito.data.dao.CierreConsultaRow
import com.famessa.deli_antojito.domain.model.cierre.*
import org.junit.Test
import org.junit.Assert.*
class CierreConsultaMappersTest {
    private val row = CierreConsultaRow(1,1000,300,1,7,"Alitas",2,100,200,0)
    @Test fun repeatedHeaderIsGrouped() {
        val result = mapCierreRows(listOf(row, row.copy(lineaId=2, productoId=8, cantidad=1, subtotalCentavos=100, posicion=1))) as ConsultaSnapshotsResult.Success
        assertEquals(1, result.pedidos.size); assertEquals(2, result.pedidos.single().lineas.size)
    }
    @Test fun missingLineRejected() { assertTrue(mapCierreRows(listOf(row.copy(lineaId=null))) is ConsultaSnapshotsResult.InvalidData) }
    @Test fun nullableFieldRejected() { assertTrue(mapCierreRows(listOf(row.copy(nombre=null))) is ConsultaSnapshotsResult.InvalidData) }
    @Test fun negativeAmountRejected() { assertTrue(mapCierreRows(listOf(row.copy(totalCentavos=-1))) is ConsultaSnapshotsResult.InvalidData) }
    @Test fun inconsistentHeadersRejected() { assertTrue(mapCierreRows(listOf(row, row.copy(totalCentavos=1))) is ConsultaSnapshotsResult.InvalidData) }
    @Test fun zeroRowsIsEmpty() { assertEquals(ConsultaSnapshotsResult.Success(emptyList()), mapCierreRows(emptyList())) }
}
