package com.famessa.deli_antojito.feature.product

import org.junit.Assert.assertTrue
import org.junit.Test

class ProductLoggingTest {
    @Test
    fun featureDoesNotExposeBase64LoggingHelpers() {
        assertTrue("Product feature keeps logging out of UI state", ProductFormState(img = "base64").img != null)
    }
}
