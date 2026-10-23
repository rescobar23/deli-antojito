package com.famessa.deli_antojito.domain.usecase.pedido

import com.famessa.deli_antojito.domain.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Test

class ChangePedidoStateUseCaseTest {
    @Test fun exactlyFourOfSixteenPairsAreAllowed() {
        val allowed = setOf(
            EstadoPedido.PENDIENTE to EstadoPedido.EN_PREPARACION,
            EstadoPedido.PENDIENTE to EstadoPedido.CANCELADO,
            EstadoPedido.EN_PREPARACION to EstadoPedido.COMPLETADO,
            EstadoPedido.EN_PREPARACION to EstadoPedido.CANCELADO
        )
        for (from in EstadoPedido.entries) for (to in EstadoPedido.entries) {
            assertEquals(from to to in allowed, canTransition(from, to))
        }
    }
}
