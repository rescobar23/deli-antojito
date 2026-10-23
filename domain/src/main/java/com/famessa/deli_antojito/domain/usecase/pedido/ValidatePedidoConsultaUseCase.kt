package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*

/** Checks inclusive ranges before the infrastructure converts civil dates to epoch boundaries. */
class ValidatePedidoConsultaUseCase {
    operator fun invoke(query: PedidoConsulta): Boolean {
        if (query.minimo != null && query.maximo != null && query.minimo.centavos > query.maximo.centavos) return false
        fun key(date: com.famessa.deli_antojito.domain.time.LocalDateParts) =
            date.year.toLong() * 10000 + date.month * 100 + date.day
        return query.desde == null || query.hasta == null || key(query.desde) <= key(query.hasta)
    }
}

