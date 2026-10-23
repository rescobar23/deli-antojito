package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.PedidoRepository

/** Updates pending content; storage rechecks revision and state before committing. */
class UpdatePedidoUseCase(private val repository: PedidoRepository) {
    suspend operator fun invoke(command: ActualizarPedidoCommand): PedidoWriteResult {
        val reason = ValidatePedidoUseCase()(command.draft)
        if (reason != null) return PedidoWriteResult.ValidationError(reason)
        return repository.actualizar(command.copy(draft = command.draft.normalized()))
    }
}

