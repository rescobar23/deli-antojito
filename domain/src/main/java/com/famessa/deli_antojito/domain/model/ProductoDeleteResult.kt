package com.famessa.deli_antojito.domain.model

sealed interface ProductoDeleteResult {
    data object Success : ProductoDeleteResult
    data object ProductNotFound : ProductoDeleteResult
    data class PersistenceError(val cause: Throwable? = null) : ProductoDeleteResult
}
