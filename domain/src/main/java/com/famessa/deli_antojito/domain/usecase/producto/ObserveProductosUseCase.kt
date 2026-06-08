package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.repository.ProductoRepository

class ObserveProductosUseCase(
    private val productoRepository: ProductoRepository
) {
    operator fun invoke() = productoRepository.observeProductos()
}
