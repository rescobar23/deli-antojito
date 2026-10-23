package com.famessa.deli_antojito.feature.batchclosing
import androidx.lifecycle.SavedStateHandle
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.usecase.cierre.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
@OptIn(ExperimentalCoroutinesApi::class)
class BatchClosingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val clock = BatchClock()
    private val resolver = BatchResolver()
    private val repository = BatchRepository()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }
    private fun vm(saved: SavedStateHandle = SavedStateHandle()) = BatchClosingViewModel(GetConsultaCierreUseCase(repository,clock::nowMillis,ValidateCierrePeriodoUseCase(resolver::isValidZone),CalculateConsultaCierreUseCase(),dispatcher),clock,resolver,saved,dispatcher)
    @Test fun initializesMidnightAndEmpty() = runTest(dispatcher) {
        val vm = vm(); advanceUntilIdle()
        assertEquals(0L, vm.state.value.periodo?.inicioMillis)
        assertEquals(3000L, vm.state.value.periodo?.finExclusivoMillis)
        assertEquals(BatchStatus.Empty, vm.state.value.status)
    }
    @Test fun errorThenRetryPreservesPeriod() = runTest(dispatcher) {
        repository.answer=ConsultaSnapshotsResult.ReadFailure
        val vm=vm(); advanceUntilIdle(); assertEquals(BatchStatus.Error,vm.state.value.status)
        repository.answer=ConsultaSnapshotsResult.Success(emptyList()); vm.refresh(); advanceUntilIdle()
        assertEquals(BatchStatus.Empty,vm.state.value.status); assertEquals(3000L,vm.state.value.periodo?.finExclusivoMillis)
    }
    @Test fun invalidDoesNotRead() = runTest(dispatcher) {
        val vm=vm(); advanceUntilIdle(); val calls=repository.calls
        vm.selectPeriod(CierrePeriodo(3000,2000,"UTC")); advanceUntilIdle()
        assertEquals(BatchStatus.InvalidPeriod,vm.state.value.status); assertEquals(calls,repository.calls)
    }
    @Test fun restoresLegacySelectionAsWholeDayAndRefreshesToday() = runTest(dispatcher) {
        val vm=vm(SavedStateHandle(mapOf("batch_start" to 1000L,"batch_end" to 2000L))); advanceUntilIdle()
        assertEquals(0L,vm.state.value.periodo?.inicioMillis)
        assertEquals(3000L,vm.state.value.periodo?.finExclusivoMillis)
        clock.now=4500; vm.onResume(); advanceUntilIdle()
        assertEquals(4500L,vm.state.value.periodo?.finExclusivoMillis)
    }
    @Test fun resumeZoneChangePreservesInstants() = runTest(dispatcher) {
        val vm=vm(); advanceUntilIdle(); resolver.zone="GMT"; vm.onResume(); advanceUntilIdle()
        assertEquals("GMT",vm.state.value.periodo?.zonaId); assertEquals(3000L,vm.state.value.periodo?.finExclusivoMillis)
    }
    @Test fun editingCancelPreservesSelection() = runTest(dispatcher) {
        val vm=vm(); advanceUntilIdle(); val period=vm.state.value.periodo
        vm.beginEdit(); assertNotNull(vm.state.value.editing); vm.cancelEdit()
        assertNull(vm.state.value.editing); assertEquals(period,vm.state.value.periodo)
    }
    @Test fun pastDateQueriesOnlyThatDayAndPersistsCivilSelection() = runTest(dispatcher) {
        val saved = SavedStateHandle()
        val vm=vm(saved); advanceUntilIdle()
        vm.selectDate(LocalDateParts(1969,12,31)); advanceUntilIdle()
        assertEquals(-86400000L,vm.state.value.periodo?.inicioMillis)
        assertEquals(0L,vm.state.value.periodo?.finExclusivoMillis)
        val restored = vm(saved); advanceUntilIdle()
        assertEquals(vm.state.value.periodo,restored.state.value.periodo)
    }
    @Test fun futureAndInvalidDatesKeepSelectionWithoutReading() = runTest(dispatcher) {
        val vm=vm(); advanceUntilIdle(); val period=vm.state.value.periodo; val calls=repository.calls
        vm.beginEdit()
        vm.selectDate(LocalDateParts(1970,1,2)); advanceUntilIdle()
        assertNotNull(vm.state.value.editing?.error)
        vm.selectDate(LocalDateParts(1970,2,30)); advanceUntilIdle()
        assertEquals(period,vm.state.value.periodo)
        assertEquals(calls,repository.calls)
    }
    @Test fun selectedDayUsesLocalMidnightsAcrossDaylightSaving() = runTest(dispatcher) {
        clock.zone="America/New_York"; resolver.zone=clock.zone
        clock.now=1741651200000L
        val vm=vm(); advanceUntilIdle()
        vm.selectDate(LocalDateParts(2025,3,9)); advanceUntilIdle()
        val period = requireNotNull(vm.state.value.periodo)
        assertEquals(1741496400000L,period.inicioMillis)
        assertEquals(23L*60*60*1000,period.finExclusivoMillis-period.inicioMillis)
    }
    @Test fun readyPublishesSummaryAndRankingTogether() = runTest(dispatcher) {
        val line=LineaConsultaSnapshot(1,7,"Histórico",2,com.famessa.deli_antojito.domain.model.Money(100),com.famessa.deli_antojito.domain.model.Money(200),0)
        repository.answer=ConsultaSnapshotsResult.Success(listOf(PedidoConsultaSnapshot(1,1000,com.famessa.deli_antojito.domain.model.Money(200),listOf(line))))
        val vm=vm(); advanceUntilIdle()
        assertEquals(BatchStatus.Ready,vm.state.value.status)
        assertEquals(200L,vm.state.value.consulta?.resumen?.bruto?.centavos)
        assertEquals(7L,vm.state.value.consulta?.topProductos?.single()?.productoId)
    }
    @Test fun integrityAndInvalidZoneHaveExplicitErrors() = runTest(dispatcher) {
        repository.answer=ConsultaSnapshotsResult.InvalidData(CierreDataError.MissingLines)
        val vm=vm(); advanceUntilIdle(); assertEquals(BatchStatus.Error,vm.state.value.status)
        vm.selectPeriod(CierrePeriodo(0,1000,"bad-zone")); advanceUntilIdle()
        assertEquals(BatchStatus.InvalidPeriod,vm.state.value.status)
    }
    @Test fun lateCancelledResponseCannotOverwriteNewPeriod() = runTest(dispatcher) {
        val first=CompletableDeferred<Unit>()
        var calls=0
        val reader=object : com.famessa.deli_antojito.domain.repository.CierreConsultaRepository {
            override suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult {
                if(++calls==1) withContext(NonCancellable) { first.await() }
                return ConsultaSnapshotsResult.Success(emptyList())
            }
        }
        val vm=BatchClosingViewModel(GetConsultaCierreUseCase(reader,clock::nowMillis,ValidateCierrePeriodoUseCase(resolver::isValidZone),CalculateConsultaCierreUseCase(),dispatcher),clock,resolver,SavedStateHandle(),dispatcher)
        runCurrent(); vm.selectPeriod(CierrePeriodo(1000,2000,"UTC")); runCurrent()
        assertEquals(BatchStatus.Empty,vm.state.value.status)
        first.complete(Unit); advanceUntilIdle()
        assertEquals(1000L,vm.state.value.periodo?.inicioMillis)
        assertEquals(2000L,vm.state.value.consulta?.periodo?.finExclusivoMillis)
    }    @Test fun completedReadDoesNotDismissAnOpenEditor() = runTest(dispatcher) {
        val release=CompletableDeferred<Unit>()
        val reader=object : com.famessa.deli_antojito.domain.repository.CierreConsultaRepository {
            override suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult { release.await(); return ConsultaSnapshotsResult.Success(emptyList()) }
        }
        val vm=BatchClosingViewModel(GetConsultaCierreUseCase(reader,clock::nowMillis,ValidateCierrePeriodoUseCase(resolver::isValidZone),CalculateConsultaCierreUseCase(),dispatcher),clock,resolver,SavedStateHandle(),dispatcher)
        runCurrent(); vm.beginEdit(); release.complete(Unit); advanceUntilIdle()
        assertNotNull(vm.state.value.editing)
        assertEquals(BatchStatus.Empty,vm.state.value.status)
    }}





