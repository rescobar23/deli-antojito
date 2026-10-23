package com.famessa.deli_antojito.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

/** Typed app destinations encoded into a compact saveable back stack. */
sealed interface MainDestination {
    data object Home : MainDestination
    data object Products : MainDestination
    data object Orders : MainDestination
    data object Config : MainDestination
    data class ProductEdit(val id: Long) : MainDestination
    data class OrderDetail(val id: Long) : MainDestination
    data class OrderForm(val id: Long?, val entry: String) : MainDestination
}

internal fun decodeDestination(value: String): MainDestination {
    val parts = value.split(':')
    return when (parts.firstOrNull()) {
        "products" -> MainDestination.Products
        "orders" -> MainDestination.Orders
        "config" -> MainDestination.Config
        "product" -> parts.getOrNull(1)?.toLongOrNull()?.let { MainDestination.ProductEdit(it) } ?: MainDestination.Products
        "detail" -> parts.getOrNull(1)?.toLongOrNull()?.let { MainDestination.OrderDetail(it) } ?: MainDestination.Orders
        "form" -> MainDestination.OrderForm(parts.getOrNull(1)?.toLongOrNull()?.takeIf { it > 0 }, value)
        else -> MainDestination.Home
    }
}

/** Compact saveable back stack shared by the host and deterministic navigation tests. */
@Stable
class MainNavigationState(initial: List<String> = listOf("home")) {
    var stack by mutableStateOf(initial.ifEmpty { listOf("home") })
        private set
    val destination: MainDestination get() = decodeDestination(stack.last())
    fun navigate(value: String) { stack = stack + value }
    fun back() { if (stack.size > 1) stack = stack.dropLast(1) }
    fun section(value: String) { stack = if (value == "home") listOf("home") else listOf("home", value) }
}

@Composable fun rememberMainNavigationState(): MainNavigationState = rememberSaveable(
    saver = listSaver(save = { it.stack }, restore = { MainNavigationState(it) })
) { MainNavigationState() }
