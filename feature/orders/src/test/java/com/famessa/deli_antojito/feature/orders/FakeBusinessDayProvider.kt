package com.famessa.deli_antojito.feature.orders

import com.famessa.deli_antojito.domain.time.*
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBusinessDayProvider : BusinessDayProvider {
    var millis: Long = 0L
    val day = MutableStateFlow(BusinessDay(LocalDateParts(2026, 10, 21), 0L, 86_400_000L))
    override fun nowMillis(): Long = millis
    override fun observeDay() = day
    override fun range(from: LocalDateParts?, through: LocalDateParts?) = EpochRange(
        if (from == null) null else day.value.inicio,
        if (through == null) null else day.value.finExclusivo
    )
}
