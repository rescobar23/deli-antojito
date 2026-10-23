package com.famessa.deli_antojito.domain.usecase.cierre
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*

internal val period = CierrePeriodo(1000, 2000, "UTC")
internal fun line(id: Long = 1, product: Long = 7, quantity: Int = 2, cents: Long = 100) =
    LineaConsultaSnapshot(id, product, "Producto$product", quantity, Money(cents), Money(cents * quantity), (id - 1).toInt())
internal fun fixture() = listOf(
    PedidoConsultaSnapshot(1, 1000, Money(300), listOf(line(), line(2, 8, 1))),
    PedidoConsultaSnapshot(2, 1500, Money(200), listOf(line(3))))
