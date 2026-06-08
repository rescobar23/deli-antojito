package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.repository.ProductoRepository

class ValidateProductoNameAvailabilityUseCase(
    private val productoRepository: ProductoRepository
) {
    suspend operator fun invoke(nombre: String, excludeId: Long? = null): Boolean {
        return productoRepository.isNombreDisponible(nombre, excludeId)
    }
}
