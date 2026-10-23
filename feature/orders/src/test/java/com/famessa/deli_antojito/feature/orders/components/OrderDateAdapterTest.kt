package com.famessa.deli_antojito.feature.orders.components

import com.famessa.deli_antojito.domain.time.LocalDateParts
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class OrderDateAdapterTest {
    @Test fun pickerCivilDateDoesNotShiftToPreviousLocalDay() {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { clear(); set(2026, 9, 21) }.timeInMillis
        assertEquals(LocalDateParts(2026, 10, 21), orderDateSelection(utc))
        assertEquals("2026-10-21", orderDateSelection(utc).inputText())
        assertEquals(LocalDateParts(2026, 10, 21), parseOrderDate("2026-10-21"))
        assertNull(parseOrderDate(""))
        assertThrows(IllegalArgumentException::class.java) { parseOrderDate("2026-02-30") }
    }
}
