package com.famessa.deli_antojito.domain.model

import java.text.Normalizer
import java.util.Locale
import java.math.BigDecimal

/** Case-insensitive NFC key; accents and SQL wildcard characters remain literal. */
fun normalizePedidoSearch(text: String): String =
    Normalizer.normalize(text.trim().removePrefix("#"), Normalizer.Form.NFC).lowercase(Locale.ROOT)

/** Null means unrestricted; an empty non-null set means mutually exclusive criteria. */
fun effectivePedidoStates(query: PedidoConsulta): Set<EstadoPedido>? {
    val advanced = query.estadosAvanzados.takeIf { it.isNotEmpty() }
    val tab = query.estadoPestana?.let { setOf(it) }
    return when { tab == null -> advanced; advanced == null -> tab; else -> tab.intersect(advanced) }
}

/** Parses nonnegative decimal input with at most two fraction digits, never using binary floating point. */
fun parsePedidoAmount(text: String): Money {
    val value = text.trim()
    require(Regex("[0-9]+([.,][0-9]{1,2})?").matches(value)) { "Invalid amount" }
    return try {
        Money(BigDecimal(value.replace(',', '.')).movePointRight(2).longValueExact())
    } catch (failure: ArithmeticException) {
        throw IllegalArgumentException("Amount overflow", failure)
    }
}

