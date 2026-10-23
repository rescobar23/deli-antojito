package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.PedidoRepository

/** Validates a new order and forwards its stable idempotency token to atomic persistence. */
class CreatePedidoUseCase(private val repository: PedidoRepository) {
    suspend operator fun invoke(command: CrearPedidoCommand): PedidoWriteResult {
        if (command.token.isBlank()) return PedidoWriteResult.ValidationError(PedidoValidationReason.TOKEN_INVALIDO)
        val reason = ValidatePedidoUseCase()(command.draft)
        if (reason != null) return PedidoWriteResult.ValidationError(reason)
        if (command.draft.lineas.any { it is PedidoDraftLinea.Existente })
            return PedidoWriteResult.ValidationError(PedidoValidationReason.LINEA_INVALIDA)
        return repository.crear(command.copy(draft = command.draft.normalized()))
    }
}

