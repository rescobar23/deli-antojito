package com.famessa.deli_antojito.domain.model

/** Catalog snapshot captured when adding an active product, never recaptured for an existing line. */
data class PedidoProductSnapshot(val productoId: Long, val nombre: String, val precio: Money)

/** Input distinguishes an existing persisted line from a newly captured product. */
sealed interface PedidoDraftLinea {
    val cantidad: Int
    data class Existente(val lineaId: Long, override val cantidad: Int) : PedidoDraftLinea
    data class Nueva(val snapshot: PedidoProductSnapshot, override val cantidad: Int) : PedidoDraftLinea
}

/** User-editable content only; identity, state, dates and calculated totals are not inputs. */
data class PedidoDraft(val cliente: String, val lineas: List<PedidoDraftLinea>, val observaciones: String? = null)

/** Stable per-form token makes creation safe to retry after a lost response. */
data class CrearPedidoCommand(val token: String, val draft: PedidoDraft)

/** Expected revision and persisted line identifiers prevent silent overwrites or snapshot tampering. */
data class ActualizarPedidoCommand(val id: Long, val expectedRevision: Long, val draft: PedidoDraft)

/** Request a single authorized transition against the currently known revision. */
data class CambiarEstadoPedidoCommand(val id: Long, val expectedRevision: Long, val destino: EstadoPedido)
