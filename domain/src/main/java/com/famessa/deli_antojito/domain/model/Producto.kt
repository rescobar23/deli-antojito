package com.famessa.deli_antojito.domain.model

/**
 * Product offered by the business.
 *
 * @property id Local generated identifier. Zero means a product not yet saved.
 * @property nombre Business-visible unique name.
 * @property precioBase Base sale price; must be greater than zero.
 * @property img Optional base64 JPG/PNG image payload.
 * @property activo Whether the product is available for sale.
 */
data class Producto(
    val id: Long = 0,
    val nombre: String,
    val precioBase: Double,
    val img: String? = null,
    val activo: Boolean = true
)
