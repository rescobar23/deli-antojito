package com.famessa.deli_antojito.domain.repository
import com.famessa.deli_antojito.domain.model.cierre.*

/** Reads completed orders by creation time, never creating lots or mutating orders. */
interface CierreConsultaRepository {
    /** Returns headers and lines from one snapshot in [periodo]; cancellation propagates. */
    suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult
}
