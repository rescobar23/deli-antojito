package com.famessa.deli_antojito.data.cierre
import com.famessa.deli_antojito.data.repository.CierreConsultaRepositoryImpl
import com.famessa.deli_antojito.domain.model.cierre.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
class CierreConsultaRepositoryTest {
    @Test fun completedByCreationWithExclusiveEnd() = runTest {
        CierreRoomFixtures().use { fixture ->
            fixture.insert(1000); fixture.insert(1500); fixture.insert(2000); fixture.insert(900)
            fixture.insert(1200,"PENDIENTE"); fixture.insert(1300,"EN_PREPARACION"); fixture.insert(1400,"CANCELADO")
            val result = CierreConsultaRepositoryImpl(fixture.database.pedidoDao()).readCompleted(CierrePeriodo(1000,2000,"UTC")) as ConsultaSnapshotsResult.Success
            assertEquals(listOf(1000L,1500L), result.pedidos.map { it.fechaCreacion }.sorted())
        }
    }
    @Test fun missingLinesIsError() = runTest {
        CierreRoomFixtures().use { fixture ->
            fixture.insert(1000,lines=false)
            assertTrue(CierreConsultaRepositoryImpl(fixture.database.pedidoDao()).readCompleted(CierrePeriodo(1000,2000,"UTC")) is ConsultaSnapshotsResult.InvalidData)
        }
    }
}
