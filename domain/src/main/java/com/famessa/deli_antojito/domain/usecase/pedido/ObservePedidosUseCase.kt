package com.famessa.deli_antojito.domain.usecase.pedido

import com.famessa.deli_antojito.domain.model.PedidoConsulta
import com.famessa.deli_antojito.domain.repository.PedidoRepository

/** Observes current order cards without exposing storage types. */
class ObservePedidosUseCase(private val repository: PedidoRepository) {
    operator fun invoke(consulta: PedidoConsulta = PedidoConsulta()) = repository.observePedidos(consulta)
    fun observeTotal() = repository.observeTotalPedidos()
}
