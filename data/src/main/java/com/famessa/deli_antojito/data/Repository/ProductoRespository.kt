package com.famessa.deli_antojito.data.repository

import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Producto
import kotlinx.coroutines.flow.Flow

interface IProductoRepository {
    suspend fun insertProducto(producto: Producto)
    fun getAllProductos(): Flow<List<Producto>>
    suspend fun getProductoById(id: Long): Producto?
    suspend fun deleteProducto(id: Long)
}

class ProductoRepository(private val database: AppDatabase) : IProductoRepository {

    private val productoDao = database.productoDao()

    override suspend fun insertProducto(producto: Producto) {
        productoDao.insertProducto(producto)
    }

    override fun getAllProductos(): Flow<List<Producto>> {
        return productoDao.getAllProductos()
    }

    override suspend fun getProductoById(id: Long): Producto? {
        return productoDao.getProductoById(id)
    }

    override suspend fun deleteProducto(id: Long) {
        productoDao.deleteProducto(id)
    }
}