package com.famessa.deli_antojito.feature.batchclosing
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.repository.CierreConsultaRepository
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import java.util.TimeZone
internal class BatchClock : BusinessDayProvider {
    var now = 3000L
    var zone = "UTC"
    override fun nowMillis() = now
    override fun observeDay() = flowOf(BusinessDay(LocalDateParts(1970,1,1),0,86400000))
    override fun range(from: LocalDateParts?, through: LocalDateParts?): EpochRange {
        fun midnight(date: LocalDateParts) = Calendar.getInstance(TimeZone.getTimeZone(zone)).apply {
            clear(); isLenient = false; set(date.year, date.month - 1, date.day)
        }
        return EpochRange(from?.let { midnight(it).timeInMillis },
            through?.let { midnight(it).apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis })
    }
}
internal class BatchResolver : CierreDateTimeResolver {
    var zone = "UTC"
    var resolution: CierreTimeResolution = CierreTimeResolution.Unique(CierreTimeCandidate(1000,0))
    override fun zoneId() = zone
    override fun isValidZone(zoneId: String) = zoneId in listOf("UTC","GMT","America/New_York")
    override fun toLocal(epochMillis: Long, zoneId: String): CierreLocalInstant {
        val local = Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply { timeInMillis = epochMillis }
        return CierreLocalInstant(CierreLocalDateTime(local.get(Calendar.YEAR), local.get(Calendar.MONTH) + 1,
            local.get(Calendar.DAY_OF_MONTH), local.get(Calendar.HOUR_OF_DAY), local.get(Calendar.MINUTE), local.get(Calendar.SECOND)), 0)
    }
    override fun resolve(parts: CierreLocalDateTime, zoneId: String) = resolution
}
internal class BatchRepository : CierreConsultaRepository {
    var calls = 0
    var answer: ConsultaSnapshotsResult = ConsultaSnapshotsResult.Success(emptyList())
    override suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult { calls++; return answer }
}
