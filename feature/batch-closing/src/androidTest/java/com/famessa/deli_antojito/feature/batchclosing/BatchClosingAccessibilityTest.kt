package com.famessa.deli_antojito.feature.batchclosing
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.famessa.deli_antojito.domain.model.cierre.CierrePeriodo
import org.junit.Rule
import org.junit.Test
class BatchClosingAccessibilityTest {
    @get:Rule val compose=createComposeRule()
    @Test fun labelsSurviveLargeTextAndCompactWidth() {
        compose.setContent { CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density,2f)) {
            Box(Modifier.width(320.dp)) { BatchClosingScreen(BatchClosingUiState(CierrePeriodo(0,1000,"UTC"),BatchStatus.Empty), {}, {}, {}, {}, {}) }
        } }
        compose.onNodeWithContentDescription("Volver a Inicio").assertExists()
        compose.onNodeWithTag("batch_period_start").assertHasClickAction()
        compose.onNodeWithText("No representa cobros confirmados").assertExists()
    }
    @Test fun wideNormalTextRetainsFinancialAvailabilityLabels() {
        compose.setContent { Box(Modifier.width(600.dp)) { BatchClosingScreen(BatchClosingUiState(CierrePeriodo(0,1000,"UTC"),BatchStatus.Empty), {}, {}, {}, {}, {}) } }
        compose.onNodeWithTag("batch_timezone").assertTextContains("UTC",substring=true)
        compose.onAllNodesWithText("No disponible").assertCountEquals(6)
    }}

