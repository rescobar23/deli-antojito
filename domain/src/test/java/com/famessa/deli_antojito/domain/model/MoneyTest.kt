package com.famessa.deli_antojito.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MoneyTest {
    @Test fun capturesDecimalCatalogPriceWithHalfUp() {
        assertEquals(1250L, Money.fromCatalogPrice(12.50).centavos)
        assertEquals(101L, Money.fromCatalogPrice(1.005).centavos)
        assertEquals(100L, Money.fromCatalogPrice(1.004).centavos)
    }

    @Test fun rejectsInvalidCatalogPrices() {
        listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
            0.0, -1.0, 0.001, Double.MAX_VALUE).forEach { price ->
            assertThrows(IllegalArgumentException::class.java) { Money.fromCatalogPrice(price) }
        }
    }

    @Test fun calculatesExactOrderTotal() {
        val total = Money.fromCatalogPrice(12.50) * 2 + Money.fromCatalogPrice(7.10) * 3
        assertEquals(4630L, total.centavos)
    }

    @Test fun rejectsNegativeMoneyAndNonPositiveQuantities() {
        assertThrows(IllegalArgumentException::class.java) { Money(-1) }
        assertThrows(IllegalArgumentException::class.java) { Money(1) * 0 }
        assertThrows(IllegalArgumentException::class.java) { Money(1) * -1 }
        assertEquals(0L, Money(0).centavos)
    }

    @Test fun detectsAdditionAndMultiplicationOverflow() {
        assertThrows(IllegalArgumentException::class.java) { Money(Long.MAX_VALUE) + Money(1) }
        assertThrows(IllegalArgumentException::class.java) { Money(Long.MAX_VALUE) * 2 }
        assertEquals(Long.MAX_VALUE, (Money(Long.MAX_VALUE) * 1).centavos)
    }
}
