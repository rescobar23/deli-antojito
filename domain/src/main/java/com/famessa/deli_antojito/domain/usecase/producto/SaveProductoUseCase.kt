package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import com.famessa.deli_antojito.domain.model.ProductoValidationResult
import com.famessa.deli_antojito.domain.repository.ProductoRepository

class SaveProductoUseCase(
    private val productoRepository: ProductoRepository,
    private val validateProductoUseCase: ValidateProductoUseCase
) {
    suspend operator fun invoke(
        producto: Producto,
        imageMimeType: String? = null,
        imageSizeBytes: Long? = null
    ): ProductoSaveResult {
        val validation = validateProductoUseCase(
            nombre = producto.nombre,
            precioBase = producto.precioBase,
            imageMimeType = imageMimeType,
            imageSizeBytes = imageSizeBytes
        )
        if (validation !is ProductoValidationResult.Valid) {
            return ProductoSaveResult.InvalidProduct
        }
        return productoRepository.saveProducto(producto.copy(nombre = producto.nombre.trim()))
    }
}
