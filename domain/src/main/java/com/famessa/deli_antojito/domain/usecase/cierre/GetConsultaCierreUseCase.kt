package com.famessa.deli_antojito.domain.usecase.cierre
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.repository.CierreConsultaRepository
import kotlinx.coroutines.*

/** Coordinates validation, a single read and pure calculation on an injected worker dispatcher. */
class GetConsultaCierreUseCase(private val repository: CierreConsultaRepository, private val nowMillis: () -> Long,
    private val validate: ValidateCierrePeriodoUseCase, private val calculate: CalculateConsultaCierreUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    /** Reads only valid ranges; cancellation propagates, and data/storage errors remain explicit. */
    suspend operator fun invoke(periodo: CierrePeriodo): ConsultaCierreResult = withContext(dispatcher) {
        val error = validate(periodo, nowMillis())
        if (error != null) return@withContext ConsultaCierreResult.InvalidPeriod(error)
        when (val result = repository.readCompleted(periodo)) {
            is ConsultaSnapshotsResult.Success -> calculate(periodo, result.pedidos)
            is ConsultaSnapshotsResult.InvalidData -> ConsultaCierreResult.InvalidData(result.code)
            ConsultaSnapshotsResult.ReadFailure -> ConsultaCierreResult.ReadFailure
        }
    }
}
