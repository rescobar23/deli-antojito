package com.famessa.deli_antojito.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.famessa.deli_antojito.data.entities.Producto

@Dao
interface ProductoDao {
    @Insert
    suspend fun insertProducto(producto: Producto)

    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): Producto?

    @Update
    suspend fun updateProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProducto(id: Long)
}