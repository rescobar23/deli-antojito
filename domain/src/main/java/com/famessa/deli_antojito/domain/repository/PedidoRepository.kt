package com.famessa.deli_antojito.domain.repository

import com.famessa.deli_antojito.domain.model.*
import kotlinx.coroutines.flow.Flow

/** Local order persistence boundary. Every write is indivisible and cancellation must propagate. */
interface PedidoRepository {
    /** Observes filtered lightweight cards, newest first; reports read failure explicitly. */
    fun observePedidos(consulta: PedidoConsulta): Flow<PedidoReadResult<List<PedidoResumen>>>
    /** Observes an order aggregate or NotFound, preserving historical snapshots. */
    fun observePedido(id: Long): Flow<PedidoReadResult<Pedido>>
    /** Counts all orders without filters, to distinguish an empty store from no matching results. */
    fun observeTotalPedidos(): Flow<PedidoReadResult<Int>>
    /** Observes unfiltered counts over creation timestamps in [inicio, finExclusivo). */
    fun observeResumen(inicio: Long, finExclusivo: Long): Flow<PedidoReadResult<ResumenConteos>>
    /** Creates pending order or returns the first committed order for the same stable token. */
    suspend fun crear(command: CrearPedidoCommand): PedidoWriteResult
    /** Updates only pending content at expected revision; never accepts UI totals or historical prices. */
    suspend fun actualizar(command: ActualizarPedidoCommand): PedidoWriteResult
    /** Applies a valid transition and appends its timestamped history in the same transaction. */
    suspend fun cambiarEstado(command: CambiarEstadoPedidoCommand): PedidoWriteResult
}
