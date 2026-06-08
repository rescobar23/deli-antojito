package com.famessa.deli_antojito.domain.model

sealed interface ProductoSaveResult {
    data class Success(val producto: Producto) : ProductoSaveResult
    data object InvalidProduct : ProductoSaveResult
    data object DuplicateName : ProductoSaveResult
    data object ProductNotFound : ProductoSaveResult
    data class PersistenceError(val cause: Throwable? = null) : ProductoSaveResult
}
