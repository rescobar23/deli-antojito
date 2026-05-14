package com.famessa.deli_antojito.data.repository

import com.famessa.deli_antojito.data.dao.ProductoDao
import com.famessa.deli_antojito.data.mappers.toDomain
import com.famessa.deli_antojito.data.mappers.toEntity
import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao
) : ProductoRepository {

    override suspend fun insertProducto(producto: Producto) {
        productoDao.insertProducto(producto.toEntity())
    }

    override suspend fun updateProducto(producto: Producto) {
        productoDao.updateProducto(producto.toEntity())
    }

    override fun getAllProductos(): Flow<List<Producto>> {
        return productoDao.getAllProductos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProductoById(id: Long): Producto? {
        return productoDao.getProductoById(id)?.toDomain()
    }

    override suspend fun deleteProducto(id: Long) {
        productoDao.deleteProducto(id)
    }
}
