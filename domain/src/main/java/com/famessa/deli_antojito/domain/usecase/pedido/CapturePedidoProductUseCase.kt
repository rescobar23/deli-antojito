package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.ProductoRepository

/** Captures a positive exact price from an active catalog product; null means unavailable or invalid. */
class CapturePedidoProductUseCase(private val repository: ProductoRepository) {
    suspend operator fun invoke(id: Long): PedidoProductSnapshot? {
        val product = repository.getProductoById(id) ?: return null
        if (!product.activo || product.nombre.isBlank()) return null
        return try { PedidoProductSnapshot(product.id, product.nombre, Money.fromCatalogPrice(product.precioBase)) }
        catch (_: IllegalArgumentException) { null }
    }
}

