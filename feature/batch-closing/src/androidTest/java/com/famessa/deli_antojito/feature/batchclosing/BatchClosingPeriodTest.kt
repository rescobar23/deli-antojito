package com.famessa.deli_antojito.feature.batchclosing

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.time.LocalDateParts
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class BatchClosingPeriodTest {
    @get:Rule val compose = createComposeRule()

    @Test fun singleDateFieldHasNoEndOrTime() {
        compose.setContent {
            BatchClosingScreen(BatchClosingUiState(periodo=CierrePeriodo(0,1000,"UTC"),status=BatchStatus.Empty), {}, {}, {}, {}, {})
        }
        compose.onNodeWithTag("batch_empty").assertExists()
        compose.onNodeWithTag("batch_retry").assertHasClickAction()
        compose.onNodeWithTag("batch_period_start").assertHasClickAction()
        compose.onNodeWithText("01/01/1970").assertExists()
        compose.onNodeWithTag("batch_period_end").assertDoesNotExist()
        compose.onNodeWithText("Fecha de fin", substring=true).assertDoesNotExist()
        compose.onNodeWithText("00:00", substring=true).assertDoesNotExist()
    }

    @Test fun loadingHasNoStaleGross() {
        compose.setContent { BatchClosingScreen(BatchClosingUiState(status=BatchStatus.Loading), {}, {}, {}, {}, {}) }
        compose.onNodeWithTag("batch_loading").assertExists()
        compose.onNodeWithTag("batch_gross_amount").assertDoesNotExist()
    }

    @Test fun calendarOnlyEditorCanBeCancelled() {
        var cancelled=false
        compose.setContent {
            BatchClosingScreen(BatchClosingUiState(periodo=CierrePeriodo(0,1000,"UTC"),
                status=BatchStatus.Empty,editing=BatchDateEditing(LocalDateParts(1970,1,1))),
                {}, {}, {}, {cancelled=true}, {})
        }
        compose.onNodeWithTag("batch_date_picker").assertExists()
        compose.onNodeWithText("Segundos",substring=true).assertDoesNotExist()
        compose.onNodeWithText("Hora",substring=true).assertDoesNotExist()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertTrue(cancelled) }
    }

    @Test fun applyingCalendarReturnsCivilDateWithoutUtcShift() {
        var selected: LocalDateParts? = null
        compose.setContent {
            BatchClosingScreen(BatchClosingUiState(periodo=CierrePeriodo(0,1000,"UTC"),
                status=BatchStatus.Empty,editing=BatchDateEditing(LocalDateParts(2026,10,23))),
                {}, {}, {}, {}, {selected=it})
        }
        compose.onNodeWithText("Aplicar").performClick()
        compose.runOnIdle { assertEquals(LocalDateParts(2026,10,23),selected) }
    }

    @Test fun invalidDateExplainsErrorWithoutGross() {
        compose.setContent {
            BatchClosingScreen(BatchClosingUiState(status=BatchStatus.InvalidPeriod,
                error="La fecha no puede estar en el futuro."), {}, {}, {}, {}, {})
        }
        compose.onNodeWithTag("batch_period_error").assertTextContains("La fecha",substring=true)
        compose.onNodeWithTag("batch_gross_amount").assertDoesNotExist()
    }
}


