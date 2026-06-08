package com.famessa.deli_antojito.data.repository

import com.famessa.deli_antojito.data.dao.ProductoDao
import com.famessa.deli_antojito.data.mappers.toDomain
import com.famessa.deli_antojito.data.mappers.toEntity
import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao
) : ProductoRepository {

    override fun observeProductos(): Flow<List<Producto>> {
        return productoDao.getAllProductos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProductoById(id: Long): Producto? {
        return productoDao.getProductoById(id)?.toDomain()
    }

    override suspend fun isNombreDisponible(nombre: String, excludeId: Long?): Boolean {
        return productoDao.getProductoByNormalizedName(nombre.trim(), excludeId) == null
    }

    override suspend fun saveProducto(producto: Producto): ProductoSaveResult {
        return try {
            if (!isNombreDisponible(producto.nombre, producto.id.takeIf { it > 0L })) {
                return ProductoSaveResult.DuplicateName
            }

            if (producto.id == 0L) {
                val newId = productoDao.insertProducto(producto.toEntity())
                ProductoSaveResult.Success(producto.copy(id = newId))
            } else {
                val updated = productoDao.updateProducto(producto.toEntity())
                if (updated == 0) ProductoSaveResult.ProductNotFound else ProductoSaveResult.Success(producto)
            }
        } catch (throwable: Throwable) {
            ProductoSaveResult.PersistenceError(throwable)
        }
    }

    override suspend fun deleteProducto(id: Long): ProductoDeleteResult {
        return try {
            val deleted = productoDao.deleteProducto(id)
            if (deleted == 0) ProductoDeleteResult.ProductNotFound else ProductoDeleteResult.Success
        } catch (throwable: Throwable) {
            ProductoDeleteResult.PersistenceError(throwable)
        }
    }
}
