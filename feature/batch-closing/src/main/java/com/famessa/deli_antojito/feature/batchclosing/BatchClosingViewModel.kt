package com.famessa.deli_antojito.feature.batchclosing
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.usecase.cierre.GetConsultaCierreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/** Queries a selected local day and discards stale results across cancellation races. */
@HiltViewModel
class BatchClosingViewModel @Inject constructor(private val query: GetConsultaCierreUseCase,
    private val clock: BusinessDayProvider, private val resolver: CierreDateTimeResolver,
    private val saved: SavedStateHandle,
    @BatchClosingWorker private val timeDispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    private val mutableState = MutableStateFlow(BatchClosingUiState())
    val state: StateFlow<BatchClosingUiState> = mutableState.asStateFlow()
    private var generation = 0L
    private var request: Job? = null
    private var editGeneration = 0L

    init {
        viewModelScope.launch {
            val day = clock.observeDay().first()
            val restored = saved.get<Int>("batch_year")?.let { year ->
                LocalDateParts(year, saved["batch_month"] ?: day.fecha.month, saved["batch_day"] ?: day.fecha.day)
            } ?: saved.get<Long>("batch_start")?.let { start ->
                resolver.toLocal(start, saved["batch_zone"] ?: resolver.zoneId()).parts.let {
                    LocalDateParts(it.year, it.month, it.day)
                }
            }
            selectDate(restored ?: day.fecha)
        }
    }
    fun selectPeriod(periodo: CierrePeriodo) {
        saved["batch_start"] = periodo.inicioMillis; saved["batch_end"] = periodo.finExclusivoMillis
        saved["batch_zone"] = periodo.zonaId
        generation++; val active = generation
        request?.cancel()
        val selectedDate = state.value.selectedDate
        mutableState.value = BatchClosingUiState(periodo = periodo, selectedDate = selectedDate)
        request = viewModelScope.launch {
            val result = query(periodo)
            if (active != generation) return@launch
            val nextState = when (result) {
                is ConsultaCierreResult.Success -> BatchClosingUiState(periodo, if(result.consulta.resumen.totalPedidos == 0) BatchStatus.Empty else BatchStatus.Ready, result.consulta)
                is ConsultaCierreResult.InvalidPeriod -> BatchClosingUiState(periodo, BatchStatus.InvalidPeriod, error = when(result.code) {
                    CierrePeriodoError.FutureEnd -> "El fin no puede estar en el futuro."
                    CierrePeriodoError.InvalidZone -> "La zona horaria no es válida."
                    else -> "El inicio debe ser anterior al fin."
                })
                is ConsultaCierreResult.InvalidData -> BatchClosingUiState(periodo, BatchStatus.Error, error = "No se puede calcular la consulta porque hay datos de pedidos inconsistentes.")
                ConsultaCierreResult.ReadFailure -> BatchClosingUiState(periodo, BatchStatus.Error, error = "No se pudo consultar. Intenta nuevamente.")
            }
            mutableState.value = nextState.copy(editing = mutableState.value.editing, selectedDate = selectedDate)
        }
    }
    fun refresh() { state.value.selectedDate?.let(::selectDate) }
    fun onResume() {
        clock.refresh()
        refresh()
    }
    fun beginEdit() {
        val date = state.value.selectedDate ?: return
        editGeneration++
        mutableState.update { it.copy(editing = BatchDateEditing(date)) }
    }
    fun cancelEdit() { editGeneration++; mutableState.update { it.copy(editing = null) } }
    fun selectDate(date: LocalDateParts) {
        val token = ++editGeneration
        viewModelScope.launch {
            val period = try {
                withContext(timeDispatcher) {
                    val range = clock.range(date, date)
                    val start = requireNotNull(range.inicio)
                    val end = requireNotNull(range.finExclusivo)
                    val now = clock.nowMillis()
                    require(start <= now) { "La fecha no puede estar en el futuro." }
                    CierrePeriodo(start, minOf(end, now), resolver.zoneId())
                }
            } catch (error: IllegalArgumentException) {
                if (token != editGeneration) return@launch
                mutableState.update {
                    val message = error.message ?: "La fecha no es válida."
                    it.copy(editing = it.editing?.copy(error = message), error = message,
                        selectedDate = it.selectedDate ?: date,
                        status = if (it.editing == null) BatchStatus.InvalidPeriod else it.status)
                }
                return@launch
            }
            if (token != editGeneration) return@launch
            saved["batch_year"] = date.year
            saved["batch_month"] = date.month
            saved["batch_day"] = date.day
            mutableState.update { it.copy(selectedDate = date) }
            selectPeriod(period)
        }
    }
}



