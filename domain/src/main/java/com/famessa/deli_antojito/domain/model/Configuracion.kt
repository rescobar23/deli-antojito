package com.famessa.deli_antojito.domain.model

/**
 * Local business configuration for this device.
 *
 * Exactly one active configuration is valid. The business name must be trimmed,
 * non-blank, and no longer than 100 characters before it is persisted.
 */
data class Configuracion(
    val id: Int = SINGLETON_ID,
    val nombreNegocio: String,
    val createdAt: Long = 0L,
    val updatedAt: Long = createdAt
) {
    companion object {
        const val SINGLETON_ID = 1
        const val MAX_NOMBRE_NEGOCIO_LENGTH = 100
    }
}
