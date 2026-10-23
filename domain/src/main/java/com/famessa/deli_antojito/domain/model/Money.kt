package com.famessa.deli_antojito.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

/** Exact non-negative MXN amount; invalid or overflowing inputs are rejected. */
data class Money(val centavos: Long) {
    init { require(centavos >= 0) }
    /** Adds another amount, rejecting overflow. */
    operator fun plus(other: Money): Money {
        require(centavos <= Long.MAX_VALUE - other.centavos) { "Amount overflow" }
        return Money(centavos + other.centavos)
    }
    /** Multiplies by a strictly positive integer, rejecting overflow. */
    operator fun times(quantity: Int): Money {
        require(quantity > 0) { "Quantity must be positive" }
        require(centavos <= Long.MAX_VALUE / quantity) { "Amount overflow" }
        return Money(centavos * quantity)
    }
    companion object {
        /** Captures a positive catalog price, rounding half up to two decimal places. */
        fun fromCatalogPrice(price: Double): Money {
            require(price.isFinite() && price > 0) { "Invalid catalog price" }
            val cents = try {
                BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP)
                    .movePointRight(2).longValueExact()
            } catch (failure: ArithmeticException) {
                throw IllegalArgumentException("Amount overflow", failure)
            }
            require(cents > 0) { "Rounded catalog price must be positive" }
            return Money(cents)
        }
    }
}
