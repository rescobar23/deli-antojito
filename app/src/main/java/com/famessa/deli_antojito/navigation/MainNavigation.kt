package com.famessa.deli_antojito.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.famessa.deli_antojito.feature.home.HomeView
import com.famessa.deli_antojito.feature.product.ProductListRoute
import com.famessa.deli_antojito.feature.product.ProductEditRoute
import com.famessa.deli_antojito.feature.businessconfig.BusinessConfigRoute
import com.famessa.deli_antojito.feature.orders.list.OrdersRoute
import com.famessa.deli_antojito.feature.orders.detail.OrderDetailRoute
import com.famessa.deli_antojito.feature.orders.form.OrderFormRoute
import java.util.UUID
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*

@Composable fun MainNavigation() {
    val navigation = rememberMainNavigationState()
    val stack = navigation.stack
    fun go(value: String) { navigation.navigate(value) }
    fun back() { navigation.back() }
    fun section(value: String) { navigation.section(value) }
    val current = decodeDestination(stack.last())
    val main = current == MainDestination.Home || current == MainDestination.Products || current == MainDestination.Orders || current == MainDestination.BatchClosing
    BackHandler(enabled = stack.size > 1 && current !is MainDestination.OrderForm) { back() }
    Scaffold(bottomBar = {
        if (main) NavigationBar(containerColor = Color.White) {
            listOf(
                Triple("home", "Inicio", OrderIcon.Home),
                Triple("products", "Productos", OrderIcon.Box),
                Triple("orders", "Pedidos", OrderIcon.Orders),
                Triple("close", "Cierre de lote", OrderIcon.Archive),
                Triple("config", "Configuración", OrderIcon.Settings)
            ).forEach { (route, label, icon) ->
                val selected = current == decodeDestination(route)
                NavigationBarItem(selected = selected, enabled = true,
                    onClick = { if (route == "config") go(route) else section(route) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AquaLv4, selectedTextColor = AquaLv4,
                        indicatorColor = Color.Transparent, unselectedIconColor = Color(0xFF66717B), unselectedTextColor = Color(0xFF66717B)),
                    icon = { Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Box(Modifier.width(32.dp).height(2.dp).background(if (selected) AquaLv4 else Color.Transparent))
                        Spacer(Modifier.height(5.dp))
                        OrderUiIcon(icon, modifier = Modifier.size(24.dp), tint = if (selected) AquaLv4 else Color(0xFF66717B))
                    } },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                    modifier = Modifier.testTag("nav_$route"))
            }
        }
    }) { padding -> Box(Modifier.padding(padding).fillMaxSize()) {
        when (current) {
            MainDestination.Home -> HomeView(onAdminClick = { go("config") }, onProductClick = { section("products") }, onOrdersClick = { section("orders") }, onBatchClosingClick = { section("close") }, onOpenOrder = { go("detail:$it") })
            MainDestination.BatchClosing -> com.famessa.deli_antojito.feature.batchclosing.BatchClosingRoute(onBack = { section("home") })
            MainDestination.Products -> ProductListRoute(onAddProduct = { go("product:0") }, onEditProduct = { go("product:$it") }, onBack = { section("home") })
            MainDestination.Config -> BusinessConfigRoute(isInitialSetup = false, onSaved = { back() }, onCancel = { back() })
            is MainDestination.ProductEdit -> key(stack.last()) { ProductEditRoute(productId = current.id, onSaved = { back() }, onCancel = { back() }) }
            MainDestination.Orders -> OrdersRoute(onAdd = { go("form:0:${UUID.randomUUID()}") }, onOpen = { go("detail:$it") }, onBack = { section("home") })
            is MainDestination.OrderDetail -> OrderDetailRoute(current.id, { back() }, { go("form:${current.id}:${UUID.randomUUID()}") })
            is MainDestination.OrderForm -> OrderFormRoute(current.id, current.entry, { back() }, { back() }, { section("products") })
        }
    } }
}
