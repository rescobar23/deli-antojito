package com.famessa.deli_antojito.data.time

import com.famessa.deli_antojito.domain.time.LocalDateParts
import org.junit.Assert.*
import org.junit.Test
import java.util.TimeZone

class LocalBusinessDayProviderTest {
    @Test fun midnightGapUsesFirstValidInstantOfCivilDay() {
        val calculator = LocalDayCalculator { TimeZone.getTimeZone("America/Sao_Paulo") }
        val date = LocalDateParts(2018, 11, 4)
        val start = calculator.midnight(date)
        assertEquals(date, calculator.day(start).fecha)
        assertEquals(23 * 3600000L, calculator.nextMidnight(date) - start)
    }
    @Test fun civilDayBoundariesFollowZoneAndDstInsteadOfFixedDuration() {
        val ny = LocalDayCalculator { TimeZone.getTimeZone("America/New_York") }
        val shortDay = LocalDateParts(2026, 3, 8)
        val longDay = LocalDateParts(2026, 11, 1)
        assertEquals(23 * 3600000L, ny.nextMidnight(shortDay) - ny.midnight(shortDay))
        assertEquals(25 * 3600000L, ny.nextMidnight(longDay) - ny.midnight(longDay))
    }
    @Test fun includesWholeEndDateAndAllowsOpenBounds() {
        val calculator = LocalDayCalculator { TimeZone.getTimeZone("UTC") }
        val date = LocalDateParts(2026, 10, 21)
        val range = calculator.range(date, date)
        assertEquals(calculator.midnight(date), range.inicio)
        assertEquals(calculator.nextMidnight(date), range.finExclusivo)
        assertNull(calculator.range(null, null).inicio)
        assertNull(calculator.range(null, null).finExclusivo)
        assertThrows(IllegalArgumentException::class.java) { calculator.range(LocalDateParts(2026, 10, 22), date) }
        assertThrows(IllegalArgumentException::class.java) { calculator.midnight(LocalDateParts(2026, 2, 30)) }
    }
    @Test fun zoneChangesAreReadAtEachCalculationAndMidnightStartsNewDay() {
        var zone = TimeZone.getTimeZone("UTC")
        val calculator = LocalDayCalculator { zone }
        val midnight = calculator.midnight(LocalDateParts(2026, 10, 21))
        assertEquals(LocalDateParts(2026, 10, 21), calculator.day(midnight).fecha)
        assertEquals(LocalDateParts(2026, 10, 20), calculator.day(midnight - 1).fecha)
        zone = TimeZone.getTimeZone("America/Mexico_City")
        assertEquals(LocalDateParts(2026, 10, 20), calculator.day(midnight).fecha)
    }
}
