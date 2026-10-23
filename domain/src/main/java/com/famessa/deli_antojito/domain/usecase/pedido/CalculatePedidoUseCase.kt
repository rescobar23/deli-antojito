package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*

/** Calculates new-line totals using checked cent arithmetic. Existing lines must first be resolved from storage. */
class CalculatePedidoUseCase {
    operator fun invoke(lines: List<PedidoDraftLinea>): Money = lines.fold(Money(0)) { total, line ->
        require(line is PedidoDraftLinea.Nueva) { "Resolve historical lines before calculation" }
        total + line.snapshot.precio * line.cantidad
    }
}

