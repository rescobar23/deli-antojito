package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.PedidoRepository
import com.famessa.deli_antojito.domain.time.BusinessDayProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/** Observes unfiltered creation-day counts, switching intervals whenever the local day changes. */
class ObserveDailyOrdersSummaryUseCase(private val repository: PedidoRepository, private val clock: BusinessDayProvider) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<PedidoReadResult<ResumenPedidos>> = clock.observeDay().flatMapLatest { day ->
        repository.observeResumen(day.inicio, day.finExclusivo).map { result ->
            when (result) {
                is PedidoReadResult.Success -> PedidoReadResult.Success(ResumenPedidos(day.fecha, result.value))
                PedidoReadResult.InvalidLocalData -> PedidoReadResult.InvalidLocalData
                PedidoReadResult.NotFound -> PedidoReadResult.NotFound
                PedidoReadResult.PersistenceError -> PedidoReadResult.PersistenceError
            }
        }
    }
}

