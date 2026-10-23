package com.famessa.deli_antojito.domain.time

import kotlinx.coroutines.flow.Flow

/** Civil date without an Android or storage dependency; calendar validity is checked by the adapter. */
data class LocalDateParts(val year: Int, val month: Int, val day: Int)

/** Optional half-open creation-time range in epoch milliseconds. */
data class EpochRange(val inicio: Long?, val finExclusivo: Long?)

/** Local business day and its actual midnight boundaries, which need not be 24 hours apart. */
data class BusinessDay(val fecha: LocalDateParts, val inicio: Long, val finExclusivo: Long)

/** Injectable clock and civil-date adapter. Implementations refresh on day, time-zone and resume events. */
interface BusinessDayProvider {
    /** Returns the current wall-clock epoch milliseconds. */
    fun nowMillis(): Long
    /** Signals foreground resume so an implementation can re-evaluate clock and zone changes. */
    fun refresh() { }
    /** Observes the current local day; cancellation releases all event listeners and timers. */
    fun observeDay(): Flow<BusinessDay>
    /** Maps inclusive civil dates to a half-open range; throws IllegalArgumentException for invalid dates/ranges. */
    fun range(from: LocalDateParts?, through: LocalDateParts?): EpochRange
}
