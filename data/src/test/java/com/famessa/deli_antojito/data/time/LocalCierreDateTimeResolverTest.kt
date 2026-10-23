package com.famessa.deli_antojito.data.time
import com.famessa.deli_antojito.domain.time.*
import org.junit.Test
import org.junit.Assert.*
class LocalCierreDateTimeResolverTest {
    private val resolver = LocalCierreDateTimeResolver()
    @Test fun secondsRoundTrip() {
        val parts = CierreLocalDateTime(2026, 10, 23, 12, 30, 59)
        val result = resolver.resolve(parts, "America/Mexico_City") as CierreTimeResolution.Unique
        assertEquals(parts, resolver.toLocal(result.candidate.epochMillis, "America/Mexico_City").parts)
    }
    @Test fun invalidDate() { assertEquals(CierreTimeResolution.InvalidDate, resolver.resolve(CierreLocalDateTime(2026,2,30,0,0,0), "UTC")) }
    @Test fun invalidZoneDoesNotFallback() { assertFalse(resolver.isValidZone("unknown")) }
    @Test fun missingHour() { assertEquals(CierreTimeResolution.NonexistentTime, resolver.resolve(CierreLocalDateTime(2024,3,10,2,30,0), "America/New_York")) }
    @Test fun repeatedHourHasTwoDistinctOffsets() {
        val result = resolver.resolve(CierreLocalDateTime(2024,11,3,1,30,0), "America/New_York") as CierreTimeResolution.Ambiguous
        assertEquals(2, result.candidates.size)
        assertEquals(3600000L, result.candidates[1].epochMillis - result.candidates[0].epochMillis)
    }
    @Test fun halfHourOverlap() {
        val result = resolver.resolve(CierreLocalDateTime(2024,4,7,1,45,0), "Australia/Lord_Howe") as CierreTimeResolution.Ambiguous
        assertEquals(1800000L, result.candidates[1].epochMillis - result.candidates[0].epochMillis)
    }
    @Test fun midnightAndUtc() { assertTrue(resolver.resolve(CierreLocalDateTime(2026,10,23,0,0,0), "UTC") is CierreTimeResolution.Unique) }
}
