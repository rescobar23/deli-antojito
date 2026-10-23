package com.famessa.deli_antojito.feature.batchclosing
import androidx.compose.ui.test.*
import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
class BatchClosingAvailabilityTest {
    @get:Rule val compose=createComposeRule()
    @Test fun unavailableIsNotMoneyOrAnExecutableClose() {
        var current by mutableStateOf(BatchStatus.Loading)
        compose.setContent { BatchClosingScreen(BatchClosingUiState(status=current), {}, {}, {}, {}, {}) }
        for(status in BatchStatus.entries) {
            compose.runOnIdle { current=status }
            compose.onNodeWithTag("batch_financial_unavailable").assertExists()
            compose.onAllNodesWithText("No disponible").assertCountEquals(6)
        }
        compose.onNodeWithTag("batch_financial_unavailable").assertExists()
        compose.onAllNodesWithText("No disponible").assertCountEquals(6)
        compose.onNodeWithText("Cerrar lote").assertDoesNotExist()
        compose.onNodeWithText("Historial").assertDoesNotExist()
        compose.onNodeWithText("No representa cobros confirmados").assertExists()
    }
}

