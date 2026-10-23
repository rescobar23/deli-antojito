package com.famessa.deli_antojito.data.repository
import com.famessa.deli_antojito.data.dao.PedidoDao
import com.famessa.deli_antojito.data.mappers.mapCierreRows
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.repository.CierreConsultaRepository
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject

/** Single-statement Room reader; no writes, cached financial data or customer projections. */
class CierreConsultaRepositoryImpl @Inject constructor(private val dao: PedidoDao) : CierreConsultaRepository {
    override suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult = try {
        mapCierreRows(dao.readCierreRows(periodo.inicioMillis, periodo.finExclusivoMillis))
    } catch (cancelled: CancellationException) { throw cancelled
    } catch (_: Exception) {
        Timber.e("CierreConsulta: local read failed")
        ConsultaSnapshotsResult.ReadFailure
    }
}
