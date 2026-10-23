package com.famessa.deli_antojito.domain.usecase.cierre
import com.famessa.deli_antojito.domain.model.cierre.*

/** Validates a half-open range against an injected clock and zone validator, without storage. */
class ValidateCierrePeriodoUseCase(private val validZone: (String) -> Boolean) {
    /** Null means valid; missing boundaries, unknown zones and future ends have distinct codes. */
    operator fun invoke(periodo: CierrePeriodo?, nowMillis: Long): CierrePeriodoError? = when {
        periodo == null -> CierrePeriodoError.MissingBoundary
        !validZone(periodo.zonaId) -> CierrePeriodoError.InvalidZone
        periodo.inicioMillis >= periodo.finExclusivoMillis -> CierrePeriodoError.StartNotBeforeEnd
        periodo.finExclusivoMillis > nowMillis -> CierrePeriodoError.FutureEnd
        else -> null
    }
}
