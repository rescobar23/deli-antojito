package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*

/** Validates editable content. Resolved historical prices are checked again inside the write transaction. */
class ValidatePedidoUseCase {
    operator fun invoke(draft: PedidoDraft): PedidoValidationReason? {
        if (draft.cliente.trim().isEmpty()) return PedidoValidationReason.CLIENTE_REQUERIDO
        if (draft.lineas.isEmpty()) return PedidoValidationReason.PRODUCTOS_REQUERIDOS
        if (draft.lineas.any { it.cantidad <= 0 }) return PedidoValidationReason.CANTIDAD_INVALIDA
        for (line in draft.lineas) {
            when (line) {
                is PedidoDraftLinea.Existente -> if (line.lineaId <= 0) return PedidoValidationReason.LINEA_INVALIDA
                is PedidoDraftLinea.Nueva -> if (line.snapshot.productoId <= 0 || line.snapshot.nombre.isBlank() ||
                    line.snapshot.precio.centavos <= 0) return PedidoValidationReason.IMPORTE_INVALIDO
            }
        }
        return try {
            CalculatePedidoUseCase()(draft.lineas.filterIsInstance<PedidoDraftLinea.Nueva>())
            null
        } catch (_: IllegalArgumentException) { PedidoValidationReason.IMPORTE_DEMASIADO_GRANDE }
    }
}
/** Canonical user content for persistence; does not change historical snapshot fields. */
fun PedidoDraft.normalized(): PedidoDraft = copy(cliente = cliente.trim(), observaciones = observaciones?.trim()?.takeIf { it.isNotEmpty() })

