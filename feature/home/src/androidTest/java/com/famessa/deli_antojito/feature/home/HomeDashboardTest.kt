package com.famessa.deli_antojito.feature.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.time.LocalDateParts
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import java.io.File


class HomeDashboardTest {
    @get:Rule val compose = createComposeRule()

    @Test fun dashboardRendersAtPhoneAndTabletWidths() {
        var compact by mutableStateOf(true)
        compose.setContent {
            Box(Modifier.width(if (compact) 360.dp else 720.dp)) {
                HomeContent("Mi Cafetería", state = HomeDashboardState(loadingOrders = false,
                    recentOrders = listOf(PedidoResumen(25, 25, "María López", 1, EstadoPedido.PENDIENTE, Money(125000)),
                        PedidoResumen(24, 24, "Carlos Ramírez", 1, EstadoPedido.EN_PREPARACION, Money(85000))),
                    summary = ResumenPedidos(LocalDateParts(2026, 10, 21), ResumenConteos(8, 3, 2, 3))))
            }
        }
        capture("home_phone.png")
        compose.onNodeWithTag("home_dashboard_list").performScrollToNode(hasText("Resumen del día"))
        capture("home_phone_summary.png")
        compose.runOnIdle { compact = false }
        compose.onNodeWithTag("home_dashboard_list").performScrollToIndex(0)
        capture("home_tablet.png")
        compose.onNodeWithTag("home_dashboard_list").performScrollToNode(hasText("Resumen del día"))
        capture("home_tablet_summary.png")
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onNodeWithTag("home_screen").captureToImage().asAndroidBitmap()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val folder = instrumentation.targetContext.getExternalFilesDir(null)
        val file = File(folder, name)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        // Gradle creates its artifact folder as shell; export there using the same identity.
        InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let { output ->
            val destination = File(output, name)
            val descriptor = instrumentation.uiAutomation.executeShellCommand("cp ${file.absolutePath} ${destination.absolutePath}")
            android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
        }
    }

    @Test fun recentOrderAndAllOrdersOpenTheirDestinations() {
        var opened: Long? = null
        var allOrders = false
        compose.setContent {
            HomeContent("Mi negocio", onOrdersClick = { allOrders = true }, onOpenOrder = { opened = it },
                state = HomeDashboardState(loadingOrders = false,
                    recentOrders = listOf(PedidoResumen(25, 25, "Cliente fixture", 1, EstadoPedido.PENDIENTE, Money(125000))),
                    summary = ResumenPedidos(LocalDateParts(2026, 10, 21), ResumenConteos(8, 3, 2, 3))))
        }
        compose.onNodeWithTag("home_order_25").performScrollTo().performClick()
        assertEquals(25L, opened)
        compose.onNodeWithText("Ver todos").performScrollTo().performClick()
        assertEquals(true, allOrders)
        compose.onNodeWithText("Ingredientes").assertDoesNotExist()
        compose.onNodeWithTag("home_business_config_button").performScrollTo().assertIsEnabled()
        compose.onNodeWithTag("home_close_button").performScrollTo().assertIsNotEnabled()
    }

    @Test fun emptyOrdersAndSummaryFailureRemainDistinct() {
        var retried = false
        compose.setContent {
            HomeContent("Mi negocio", state = HomeDashboardState(loadingOrders = false, summaryError = "Error del resumen"),
                onRetrySummary = { retried = true })
        }
        compose.onNodeWithText("Aún no hay pedidos registrados.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("home_dashboard_list").performScrollToNode(hasText("Reintentar resumen"))
        compose.onNodeWithText("Reintentar resumen").performClick()
        assertEquals(true, retried)
    }
}
