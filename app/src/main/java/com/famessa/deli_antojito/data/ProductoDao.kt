package com.famessa.deli_antojito.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert
    suspend fun insertProducto(producto: Producto)

    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): Producto?

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProducto(id: Long)
}