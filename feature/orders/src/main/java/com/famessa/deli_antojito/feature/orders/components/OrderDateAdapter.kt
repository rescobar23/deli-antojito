package com.famessa.deli_antojito.feature.orders.components

import com.famessa.deli_antojito.domain.time.LocalDateParts
import java.util.Calendar
import java.util.TimeZone

/** Material date selections represent a UTC civil date, not a local instant. */
fun orderDateSelection(utcMillis: Long): LocalDateParts = Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
    timeInMillis = utcMillis
    LocalDateParts(get(Calendar.YEAR), get(Calendar.MONTH) + 1, get(Calendar.DAY_OF_MONTH))
}
fun LocalDateParts.inputText(): String = "%04d-%02d-%02d".format(java.util.Locale.ROOT, year, month, day)
fun parseOrderDate(value: String): LocalDateParts? {
    if (value.isBlank()) return null
    require(Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(value))
    val parts = value.split('-').map(String::toInt)
    val date = LocalDateParts(parts[0], parts[1], parts[2])
    require(date.year in 1..9999)
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        isLenient = false; clear(); set(date.year, date.month - 1, date.day)
        timeInMillis
    }
    return date
}
