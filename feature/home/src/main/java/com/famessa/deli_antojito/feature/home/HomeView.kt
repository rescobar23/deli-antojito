package com.famessa.deli_antojito.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.model.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val HomeInk = Color(0xFF273744)
private val HomeMuted = Color(0xFF697580)
private val HomeBorder = Color(0xFFE7EBED)

@Composable
fun HomeView(
    onAdminClick: () -> Unit = {},
    onProductClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onBatchClosingClick: () -> Unit = {},
    onOpenOrder: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState()
    val state by viewModel.dashboardState.collectAsState()
    HomeContent(businessName, onAdminClick, onProductClick, onOrdersClick, state,
        onOpenOrder, viewModel::retryOrders, viewModel::retrySummary, onBatchClosingClick = onBatchClosingClick)
}

@Composable
fun HomeContent(
    businessName: String,
    onAdminClick: () -> Unit = {},
    onProductClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    state: HomeDashboardState = HomeDashboardState(),
    onOpenOrder: (Long) -> Unit = {},
    onRetryOrders: () -> Unit = {},
    onRetrySummary: () -> Unit = {},
    onBatchClosingClick: () -> Unit = {}
) {
    var menu by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Color(0xFFFAFCFC)).testTag("home_screen")) {
        Box(Modifier.testTag("home_business_name")) {
            AquaTopBar(businessName.ifBlank { "Deli Antojito" }, { menu = true },
                navigationIcon = OrderIcon.Menu, navigationDescription = "Abrir menú")
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                DropdownMenuItem(text = { Text("Productos") }, onClick = { menu = false; onProductClick() })
                DropdownMenuItem(text = { Text("Pedidos") }, onClick = { menu = false; onOrdersClick() })
                DropdownMenuItem(text = { Text("Cierre de Lote") }, onClick = { menu = false; onBatchClosingClick() })
                DropdownMenuItem(text = { Text("Configuración") }, onClick = { menu = false; onAdminClick() },
                    modifier = Modifier.testTag("home_business_config_menu_item"))
            }
        }
        LazyColumn(Modifier.fillMaxSize().testTag("home_dashboard_list"), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { WelcomeBanner() }
            item { ModuleGrid(onProductClick, onOrdersClick, onAdminClick, onBatchClosingClick) }
            item {
                DashboardSection {
                    Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Pedidos recientes", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge,
                            color = HomeInk, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onOrdersClick) {
                            Text("Ver todos", color = AquaLv4)
                            OrderUiIcon(OrderIcon.Chevron, modifier = Modifier.size(18.dp), tint = AquaLv4)
                        }
                    }
                    when {
                        state.ordersError != null -> {
                            Text(state.ordersError, Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.error)
                            TextButton(onClick = onRetryOrders) { Text("Reintentar pedidos") }
                        }
                        state.loadingOrders -> LinearProgressIndicator(Modifier.fillMaxWidth(), color = AquaLv4)
                        state.recentOrders.isEmpty() -> Text("Aún no hay pedidos registrados.", Modifier.padding(16.dp), color = HomeMuted)
                    }
                    state.recentOrders.forEach { order ->
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = HomeBorder)
                        RecentOrderRow(order) { onOpenOrder(order.id) }
                    }
                }
            }
            item { DailySummary(state, onRetrySummary) }
        }
    }
}

@Composable
private fun WelcomeBanner() {
    Box(Modifier.fillMaxWidth().heightIn(min = 180.dp).clip(RoundedCornerShape(8.dp))) {
        Image(painterResource(R.drawable.home_welcome), contentDescription = null,
            contentScale = ContentScale.Crop, alignment = Alignment.CenterEnd,
            modifier = Modifier.matchParentSize())
        Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(
            Color(0xFFEAF6F5), Color(0xFFEAF6F5).copy(alpha = 0.85f), Color.Transparent))))
        Column(Modifier.fillMaxWidth(0.68f).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = AquaLv5)
            Text("Gestiona tus productos, pedidos y negocio desde un solo lugar.",
                style = MaterialTheme.typography.bodyLarge, color = HomeMuted)
        }
    }
}

@Composable
private fun ModuleGrid(onProductClick: () -> Unit, onOrdersClick: () -> Unit, onAdminClick: () -> Unit, onBatchClosingClick: () -> Unit) {
    BoxWithConstraints {
        val modules = listOf(
            HomeModule("Productos", "Administra tu catálogo", com.famessa.deli_antojito.core.ui.R.drawable.ic_products, onProductClick, "home_products_button"),
            HomeModule("Configuración", "Configura tu negocio", com.famessa.deli_antojito.core.ui.R.drawable.ic_settings, onAdminClick, "home_business_config_button"),
            HomeModule("Pedidos", "Revisa y gestiona pedidos", com.famessa.deli_antojito.core.ui.R.drawable.ic_orders, onOrdersClick, "home_orders_button"),
            HomeModule("Cierre de Lote", "Consulta preliminar de pedidos", com.famessa.deli_antojito.core.ui.R.drawable.ic_batch_close, onBatchClosingClick, "home_close_button")
        )
        val columns = if (maxWidth >= 320.dp) 2 else 1
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            modules.chunked(columns).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { module -> ModuleCard(module, Modifier.weight(1f)) }
                }
            }
        }
    }
}

private data class HomeModule(val name: String, val subtitle: String, val icon: Int, val onClick: (() -> Unit)?, val tag: String)

@Composable
private fun ModuleCard(module: HomeModule, modifier: Modifier) {
    Surface(onClick = { module.onClick?.invoke() }, enabled = module.onClick != null,
        modifier = modifier.testTag(module.tag), shape = RoundedCornerShape(8.dp),
        color = Color.White, border = BorderStroke(1.dp, HomeBorder), shadowElevation = 1.dp) {
        BoxWithConstraints {
            val compact = maxWidth < 220.dp
            if (compact) {
                Column(Modifier.heightIn(min = 148.dp).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        ModuleIcon(module.icon)
                        Spacer(Modifier.weight(1f))
                        OrderUiIcon(OrderIcon.Chevron, modifier = Modifier.size(18.dp), tint = HomeMuted)
                    }
                    Text(module.name, color = HomeInk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(module.subtitle, color = HomeMuted, style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Row(Modifier.heightIn(min = 84.dp).padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModuleIcon(module.icon)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(module.name, color = HomeInk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(module.subtitle, color = HomeMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    OrderUiIcon(OrderIcon.Chevron, modifier = Modifier.size(18.dp), tint = if (module.onClick == null) Color(0xFFB0B9BE) else HomeInk)
                }
            }
        }
    }
}

@Composable
private fun ModuleIcon(icon: Int) {
    Surface(shape = CircleShape, color = Color(0xFFE7F3F2), modifier = Modifier.size(48.dp)) {
        Icon(painterResource(icon), contentDescription = null, tint = AquaLv4, modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun DashboardSection(content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = Color.White,
        border = BorderStroke(1.dp, HomeBorder)) { Column(content = content) }
}

private data class HomeStatus(val label: String, val icon: OrderIcon, val foreground: Color, val background: Color)
private fun EstadoPedido.presentation(): HomeStatus = when (this) {
    EstadoPedido.PENDIENTE -> HomeStatus("Pendiente", OrderIcon.Clock, Color(0xFFB87400), Color(0xFFFFF0D4))
    EstadoPedido.EN_PREPARACION -> HomeStatus("En preparación", OrderIcon.Chef, Color(0xFF005CD6), Color(0xFFE0EDFF))
    EstadoPedido.COMPLETADO -> HomeStatus("Completado", OrderIcon.Check, Color(0xFF008A16), Color(0xFFE3F5E9))
    EstadoPedido.CANCELADO -> HomeStatus("Cancelado", OrderIcon.Cancel, Color(0xFFD51C2B), Color(0xFFFFDFE2))
}

@Composable
private fun RecentOrderRow(order: PedidoResumen, onClick: () -> Unit) {
    val status = order.estado.presentation()
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).testTag("home_order_${order.id}").padding(12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(Modifier.size(44.dp), shape = CircleShape, color = status.background) {
            OrderUiIcon(status.icon, modifier = Modifier.padding(10.dp), tint = status.foreground)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("#" + order.numero.toString().padStart(5, '0'), color = HomeInk, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(order.cliente, color = HomeInk, style = MaterialTheme.typography.bodyMedium)
            Text(SimpleDateFormat("d MMM yyyy • HH:mm", Locale("es", "MX")).format(Date(order.fechaCreacion)),
                color = HomeMuted, style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(6.dp), color = status.background) {
                Text(status.label, Modifier.padding(horizontal = 8.dp, vertical = 5.dp), color = status.foreground, style = MaterialTheme.typography.labelSmall)
            }
            Text(NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(BigDecimal.valueOf(order.total.centavos, 2)),
                color = AquaLv4, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
        OrderUiIcon(OrderIcon.Chevron, modifier = Modifier.size(18.dp), tint = HomeInk)
    }
}

@Composable
private fun DailySummary(state: HomeDashboardState, retry: () -> Unit) {
    DashboardSection {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Resumen del día", Modifier.weight(1f), color = HomeInk, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                state.summary?.fecha?.let { day ->
                    val date = Calendar.getInstance().apply { clear(); set(day.year, day.month - 1, day.day) }.time
                    Text(SimpleDateFormat("d MMM yyyy", Locale("es", "MX")).format(date), color = HomeMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
            when {
                state.summaryError != null -> {
                    Text(state.summaryError, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = retry) { Text("Reintentar resumen") }
                }
                state.summary == null -> LinearProgressIndicator(Modifier.fillMaxWidth(), color = AquaLv4)
                else -> BoxWithConstraints {
                    val columns = if (maxWidth >= 600.dp) 4 else 2
                    val counts = state.summary.conteos
                    val metrics = listOf(
                        Triple("Pedidos totales", counts.totalPedidos, HomeStatus("", OrderIcon.Orders, AquaLv4, Color(0xFFEEF7F7))),
                        Triple("Pendientes", counts.pendientes, EstadoPedido.PENDIENTE.presentation()),
                        Triple("En preparación", counts.enPreparacion, EstadoPedido.EN_PREPARACION.presentation()),
                        Triple("Completados", counts.completados, EstadoPedido.COMPLETADO.presentation())
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        metrics.chunked(columns).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { (label, count, status) ->
                                    Surface(Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = status.background.copy(alpha = 0.5f)) {
                                        Row(Modifier.heightIn(min = 92.dp).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            OrderUiIcon(status.icon, modifier = Modifier.size(28.dp), tint = status.foreground)
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(count.toString(), style = MaterialTheme.typography.titleLarge, color = HomeInk, fontWeight = FontWeight.Bold)
                                                Text(label, style = MaterialTheme.typography.labelSmall, color = HomeMuted)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
