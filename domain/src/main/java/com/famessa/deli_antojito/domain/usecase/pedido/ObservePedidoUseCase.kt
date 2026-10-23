package com.famessa.deli_antojito.domain.usecase.pedido

import com.famessa.deli_antojito.domain.repository.PedidoRepository

/** Observes an order aggregate, including historical lines, or an explicit error. */
class ObservePedidoUseCase(private val repository: PedidoRepository) {
    operator fun invoke(id: Long) = repository.observePedido(id)
}
