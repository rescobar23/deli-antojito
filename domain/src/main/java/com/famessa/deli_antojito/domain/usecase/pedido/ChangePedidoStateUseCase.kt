package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.PedidoRepository

/** Complete business state-transition policy, also enforced against the current row within transactions. */
fun canTransition(from: EstadoPedido, to: EstadoPedido): Boolean = when (from) {
    EstadoPedido.PENDIENTE -> to == EstadoPedido.EN_PREPARACION || to == EstadoPedido.CANCELADO
    EstadoPedido.EN_PREPARACION -> to == EstadoPedido.COMPLETADO || to == EstadoPedido.CANCELADO
    EstadoPedido.COMPLETADO, EstadoPedido.CANCELADO -> false
}

/** Requests a state change; expected revision prevents conflicting actions. */
class ChangePedidoStateUseCase(private val repository: PedidoRepository) {
    suspend operator fun invoke(command: CambiarEstadoPedidoCommand) = repository.cambiarEstado(command)
}

