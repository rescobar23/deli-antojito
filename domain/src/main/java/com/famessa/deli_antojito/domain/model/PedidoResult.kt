package com.famessa.deli_antojito.domain.model

/** Read outcomes preserve failure versus a successful empty collection. No persistence payload reaches UI. */
sealed interface PedidoReadResult<out T> {
    data class Success<T>(val value: T) : PedidoReadResult<T>
    data object NotFound : PedidoReadResult<Nothing>
    data object InvalidLocalData : PedidoReadResult<Nothing>
    data object PersistenceError : PedidoReadResult<Nothing>
}

/** Sanitized failure reasons suitable for field-specific validation messages. */
enum class PedidoValidationReason {
    CLIENTE_REQUERIDO, PRODUCTOS_REQUERIDOS, CANTIDAD_INVALIDA, IMPORTE_INVALIDO,
    IMPORTE_DEMASIADO_GRANDE, LINEA_INVALIDA, TOKEN_INVALIDO, RANGO_INVALIDO
}

/** Atomic write outcomes; Conflict requires reload instead of an unconditional retry. */
sealed interface PedidoWriteResult {
    data class Success(val pedido: Pedido) : PedidoWriteResult
    data class ValidationError(val reason: PedidoValidationReason, val lineaId: Long? = null) : PedidoWriteResult
    data class ProductUnavailable(val productoId: Long) : PedidoWriteResult
    data object NotFound : PedidoWriteResult
    data object Conflict : PedidoWriteResult
    data object InvalidTransition : PedidoWriteResult
    data object InvalidLocalData : PedidoWriteResult
    data object PersistenceError : PedidoWriteResult
}
