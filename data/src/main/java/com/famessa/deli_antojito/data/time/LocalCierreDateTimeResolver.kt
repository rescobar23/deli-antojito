package com.famessa.deli_antojito.data.time
import com.famessa.deli_antojito.domain.time.*
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone
import javax.inject.Inject

/** Strict Calendar-based civil-time adapter compatible with API25, including gaps and overlaps. */
class LocalCierreDateTimeResolver @Inject constructor() : CierreDateTimeResolver {
    override fun zoneId(): String = TimeZone.getDefault().id
    override fun isValidZone(zoneId: String): Boolean = zoneId in TimeZone.getAvailableIDs()
    override fun toLocal(epochMillis: Long, zoneId: String): CierreLocalInstant {
        require(isValidZone(zoneId))
        val zone = TimeZone.getTimeZone(zoneId)
        val calendar = GregorianCalendar(zone).apply { timeInMillis = epochMillis }
        return CierreLocalInstant(CierreLocalDateTime(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,
            calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)),zone.getOffset(epochMillis)/1000)
    }
    override fun resolve(parts: CierreLocalDateTime, zoneId: String): CierreTimeResolution {
        if (!isValidZone(zoneId) || parts.year !in 1..9999) return CierreTimeResolution.InvalidDate
        val civilEpoch = try {
            GregorianCalendar(TimeZone.getTimeZone("UTC")).apply {
                clear(); isLenient = false
                set(parts.year, parts.month-1, parts.day, parts.hour, parts.minute, parts.second)
            }.timeInMillis
        } catch (_: IllegalArgumentException) { return CierreTimeResolution.InvalidDate }
        val zone = TimeZone.getTimeZone(zoneId)
        // Collect effective offsets around the civil date; round-trip verification rejects gaps.
        val offsets = (-48..48).map { zone.getOffset(civilEpoch + it * 3600000L) }.toSet()
        val candidates = offsets.mapNotNull { offset ->
            val epoch = civilEpoch - offset
            if (toLocal(epoch, zoneId).parts == parts) CierreTimeCandidate(epoch, offset/1000) else null
        }.distinctBy { it.epochMillis }.sortedBy { it.epochMillis }
        return when (candidates.size) {
            0 -> CierreTimeResolution.NonexistentTime
            1 -> CierreTimeResolution.Unique(candidates.single())
            else -> CierreTimeResolution.Ambiguous(candidates)
        }
    }
}
