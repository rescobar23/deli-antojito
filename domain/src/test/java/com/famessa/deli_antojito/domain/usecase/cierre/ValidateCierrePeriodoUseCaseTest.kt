package com.famessa.deli_antojito.domain.usecase.cierre
import org.junit.Test
import org.junit.Assert.*
import com.famessa.deli_antojito.domain.model.cierre.*
class ValidateCierrePeriodoUseCaseTest {
    private val validate = ValidateCierrePeriodoUseCase { it == "UTC" }
    @Test fun validIncludingNow() { assertNull(validate(period, 2000)) }
    @Test fun missing() { assertEquals(CierrePeriodoError.MissingBoundary, validate(null, 3000)) }
    @Test fun equalBoundaries() { assertEquals(CierrePeriodoError.StartNotBeforeEnd, validate(period.copy(inicioMillis = 2000), 3000)) }
    @Test fun reverseBoundaries() { assertEquals(CierrePeriodoError.StartNotBeforeEnd, validate(period.copy(inicioMillis = 3000), 3000)) }
    @Test fun future() { assertEquals(CierrePeriodoError.FutureEnd, validate(period, 1999)) }
    @Test fun invalidZone() { assertEquals(CierrePeriodoError.InvalidZone, validate(period.copy(zonaId = "unknown"), 3000)) }
}
