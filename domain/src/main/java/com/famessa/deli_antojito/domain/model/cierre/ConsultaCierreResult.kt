package com.famessa.deli_antojito.domain.model.cierre

/** Expected validation failures, kept separate from storage and corruption errors. */
enum class CierrePeriodoError { MissingBoundary, InvalidZone, StartNotBeforeEnd, FutureEnd }
/** Data-integrity errors safe to expose as codes, without original record data. */
enum class CierreDataError { MissingLines, InvalidIdentity, DuplicateLine, DuplicatePosition, InvalidQuantity,
    InvalidAmount, LineSubtotalMismatch, HeaderTotalMismatch, Overflow }
/** Result of the read-only storage boundary. Cancellation must propagate instead of becoming ReadFailure. */
sealed interface ConsultaSnapshotsResult {
    data class Success(val pedidos: List<PedidoConsultaSnapshot>) : ConsultaSnapshotsResult
    data class InvalidData(val code: CierreDataError) : ConsultaSnapshotsResult
    data object ReadFailure : ConsultaSnapshotsResult
}
/** Atomic result for summary and ranking. No partial financial success is permitted. */
sealed interface ConsultaCierreResult {
    data class Success(val consulta: ConsultaCierre) : ConsultaCierreResult
    data class InvalidPeriod(val code: CierrePeriodoError) : ConsultaCierreResult
    data class InvalidData(val code: CierreDataError) : ConsultaCierreResult
    data object ReadFailure : ConsultaCierreResult
}
