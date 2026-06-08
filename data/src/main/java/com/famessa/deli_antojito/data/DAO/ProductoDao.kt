package com.famessa.deli_antojito.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.famessa.deli_antojito.data.entities.Producto

@Dao
interface ProductoDao {
    @Insert
    suspend fun insertProducto(producto: Producto): Long

    @Query("SELECT * FROM productos ORDER BY nombre COLLATE NOCASE ASC")
    fun getAllProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): Producto?

    @Update
    suspend fun updateProducto(producto: Producto): Int

    @Query(
        """
        SELECT * FROM productos
        WHERE lower(trim(nombre)) = lower(trim(:nombre))
        AND (:excludeId IS NULL OR id != :excludeId)
        LIMIT 1
        """
    )
    suspend fun getProductoByNormalizedName(nombre: String, excludeId: Long?): Producto?

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProducto(id: Long): Int
}
