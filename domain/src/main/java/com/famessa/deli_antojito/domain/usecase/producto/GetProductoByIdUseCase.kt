package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.repository.ProductoRepository

class GetProductoByIdUseCase(
    private val productoRepository: ProductoRepository
) {
    suspend operator fun invoke(id: Long) = productoRepository.getProductoById(id)
}
