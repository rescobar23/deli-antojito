package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.BusinessNameValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateBusinessNameUseCaseTest {
    private val useCase = ValidateBusinessNameUseCase()

    @Test
    fun rejectsBlankName() {
        assertEquals(BusinessNameValidationResult.Blank, useCase(""))
    }

    @Test
    fun rejectsWhitespaceName() {
        assertEquals(BusinessNameValidationResult.Blank, useCase("   "))
    }

    @Test
    fun rejectsNameLongerThanOneHundredCharacters() {
        assertEquals(BusinessNameValidationResult.TooLong, useCase("a".repeat(101)))
    }

    @Test
    fun trimsValidName() {
        val result = useCase("  Deli Antojito  ")

        assertTrue(result is BusinessNameValidationResult.Valid)
        assertEquals("Deli Antojito", (result as BusinessNameValidationResult.Valid).trimmedName)
    }
}
