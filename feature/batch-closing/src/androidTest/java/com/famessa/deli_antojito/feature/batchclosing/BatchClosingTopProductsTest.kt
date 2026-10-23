package com.famessa.deli_antojito.feature.batchclosing
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*
import org.junit.Rule
import org.junit.Test
class BatchClosingTopProductsTest {
    @get:Rule val compose=createComposeRule()
    @Test fun historicalRankingRendersTogether() {
        val period=CierrePeriodo(0,1000,"UTC")
        compose.setContent { BatchClosingScreen(BatchClosingUiState(period,BatchStatus.Ready,ConsultaCierre(period,ResumenConsultaCierre(2,Money(500)),listOf(ProductoConsultaCierre(7,"Histórico",4,Money(400))))), {}, {}, {}, {}, {}) }
        compose.onNodeWithTag("batch_order_count").assertTextContains("2",substring=true)
        compose.onNodeWithTag("batch_top_product_7").assertExists()
        compose.onNode(hasText("Cantidad: 4") or hasText("4")).assertExists()
    }
}
