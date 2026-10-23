package com.famessa.deli_antojito.data.mappers
import com.famessa.deli_antojito.data.dao.CierreConsultaRow
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*

/** Groups repeated JOIN headers once, rejecting absent lines or invalid stored amounts explicitly. */
fun mapCierreRows(rows: List<CierreConsultaRow>): ConsultaSnapshotsResult {
    try {
        val snapshots = rows.groupBy { it.pedidoId }.map { (_, group) ->
            val header = group.first()
            if (group.any { it.fechaCreacion != header.fechaCreacion || it.totalCentavos != header.totalCentavos })
                return ConsultaSnapshotsResult.InvalidData(CierreDataError.HeaderTotalMismatch)
            val lines = group.map { row ->
                val id = row.lineaId ?: return ConsultaSnapshotsResult.InvalidData(CierreDataError.MissingLines)
                LineaConsultaSnapshot(id, row.productoId ?: return invalidRow(), row.nombre ?: return invalidRow(),
                    row.cantidad ?: return invalidRow(), Money(row.precioCentavos ?: return invalidRow()),
                    Money(row.subtotalCentavos ?: return invalidRow()), row.posicion ?: return invalidRow())
            }
            PedidoConsultaSnapshot(header.pedidoId, header.fechaCreacion, Money(header.totalCentavos), lines)
        }
        return ConsultaSnapshotsResult.Success(snapshots)
    } catch (_: IllegalArgumentException) { return invalidRow() }
}
private fun invalidRow() = ConsultaSnapshotsResult.InvalidData(CierreDataError.InvalidAmount)
