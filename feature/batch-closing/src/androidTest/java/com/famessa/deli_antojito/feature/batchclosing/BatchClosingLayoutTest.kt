package com.famessa.deli_antojito.feature.batchclosing

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.famessa.deli_antojito.domain.model.Money
import com.famessa.deli_antojito.domain.model.cierre.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

class BatchClosingLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun phoneAndTabletKeepPeriodAndRankingUsable() {
        var compact by mutableStateOf(true)
        var selected: Boolean? = null
        var refreshed = false
        val period = CierrePeriodo(1728950400000, 1729036800000, "America/Mexico_City")
        val products = listOf(
            ProductoConsultaCierre(1, "Alitas", 42, Money(252000)),
            ProductoConsultaCierre(2, "Crepas", 28, Money(168000)),
            ProductoConsultaCierre(3, "Papas", 25, Money(125000)),
            ProductoConsultaCierre(4, "Plátanos fritos", 18, Money(72000)),
            ProductoConsultaCierre(5, "Otros", 15, Money(40000)))
        compose.setContent {
            Box(Modifier.width(if (compact) 360.dp else 800.dp)) {
                BatchClosingScreen(BatchClosingUiState(period, BatchStatus.Ready,
                    ConsultaCierre(period, ResumenConsultaCierre(28, Money(685000)), products)),
                    {}, { selected = true }, { refreshed = true }, {}, {})
            }
        }
        compose.onNodeWithTag("batch_period_start").performClick()
        compose.runOnIdle { assertEquals(true, selected) }
        compose.onNodeWithTag("batch_period_end").assertDoesNotExist()
        capture("batch_phone.png")
        compose.onNodeWithTag("batch_top_product_5").performScrollTo().assertIsDisplayed()
        capture("batch_phone_products.png")
        compose.onNodeWithTag("batch_retry").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, refreshed); compact = false }
        compose.onNodeWithTag("batch_period_start").performScrollTo()
        capture("batch_tablet.png")
        compose.onNodeWithTag("batch_top_product_5").performScrollTo().assertIsDisplayed()
        capture("batch_tablet_products.png")
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onNodeWithTag("batch_closing_screen").captureToImage().asAndroidBitmap()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val file = File(instrumentation.targetContext.getExternalFilesDir(null), name)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let { output ->
            val destination = File(output, name)
            val descriptor = instrumentation.uiAutomation.executeShellCommand("cp ${file.absolutePath} ${destination.absolutePath}")
            android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
        }
    }
}
