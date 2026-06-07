package com.famessa.deli_antojito.data.mappers

import com.famessa.deli_antojito.data.entities.Producto as ProductoEntity
import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.data.entities.Configuracion as ConfiguracionEntity
import com.famessa.deli_antojito.domain.model.Configuracion

fun ProductoEntity.toDomain() = Producto(
    id = id,
    nombre = nombre,
    precioBase = precioBase,
    img = img,
    activo = activo
)

fun Producto.toEntity() = ProductoEntity(
    id = id,
    nombre = nombre,
    precioBase = precioBase,
    img = img,
    activo = activo
)

fun ConfiguracionEntity.toDomain() = Configuracion(
    id = id,
    nombreNegocio = nombreNegocio,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Configuracion.toEntity() = ConfiguracionEntity(
    id = id,
    nombreNegocio = nombreNegocio,
    createdAt = createdAt,
    updatedAt = updatedAt
)
