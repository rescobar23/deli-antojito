package com.famessa.deli_antojito.domain.time

/** Civil date/time with second precision, free of Android and storage details. */
data class CierreLocalDateTime(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int, val second: Int)
/** A possible instant and its effective UTC offset. */
data class CierreTimeCandidate(val epochMillis: Long, val offsetSeconds: Int)
/** An unambiguous local representation of an already selected instant. */
data class CierreLocalInstant(val parts: CierreLocalDateTime, val offsetSeconds: Int)
/** Strict local-time resolution; gaps and overlaps cannot be normalized silently. */
sealed interface CierreTimeResolution {
    data class Unique(val candidate: CierreTimeCandidate) : CierreTimeResolution
    data class Ambiguous(val candidates: List<CierreTimeCandidate>) : CierreTimeResolution
    data object InvalidDate : CierreTimeResolution
    data object NonexistentTime : CierreTimeResolution
}
/** Business-zone adapter; infrastructure chooses the device zone in this offline version. */
interface CierreDateTimeResolver {
    /** Current effective business zone identifier. */
    fun zoneId(): String
    /** Validates a zone identifier without silently falling back to GMT. */
    fun isValidZone(zoneId: String): Boolean
    /** Converts an instant without changing it; invalid zones throw IllegalArgumentException. */
    fun toLocal(epochMillis: Long, zoneId: String): CierreLocalInstant
    /** Strictly resolves a local time; overlapping offsets require user choice. */
    fun resolve(parts: CierreLocalDateTime, zoneId: String): CierreTimeResolution
}
