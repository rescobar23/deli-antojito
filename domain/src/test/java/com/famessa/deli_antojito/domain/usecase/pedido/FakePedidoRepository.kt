package com.famessa.deli_antojito.domain.usecase.pedido

import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow

class FakePedidoRepository : PedidoRepository {
    val list = MutableStateFlow<PedidoReadResult<List<PedidoResumen>>>(PedidoReadResult.Success(emptyList()))
    val detail = MutableStateFlow<PedidoReadResult<Pedido>>(PedidoReadResult.NotFound)
    val total = MutableStateFlow<PedidoReadResult<Int>>(PedidoReadResult.Success(0))
    val summary = MutableStateFlow<PedidoReadResult<ResumenConteos>>(PedidoReadResult.Success(ResumenConteos(0, 0, 0, 0)))
    var writeResult: PedidoWriteResult = PedidoWriteResult.PersistenceError
    val queries = mutableListOf<PedidoConsulta>()
    var lastCreate: CrearPedidoCommand? = null
    var lastUpdate: ActualizarPedidoCommand? = null
    var lastTransition: CambiarEstadoPedidoCommand? = null
    override fun observePedidos(consulta: PedidoConsulta): Flow<PedidoReadResult<List<PedidoResumen>>> { queries.add(consulta); return list }
    override fun observePedido(id: Long): Flow<PedidoReadResult<Pedido>> = detail
    override fun observeTotalPedidos(): Flow<PedidoReadResult<Int>> = total
    override fun observeResumen(inicio: Long, finExclusivo: Long): Flow<PedidoReadResult<ResumenConteos>> = summary
    override suspend fun crear(command: CrearPedidoCommand): PedidoWriteResult {
        lastCreate = command
        return writeResult
    }
    override suspend fun actualizar(command: ActualizarPedidoCommand): PedidoWriteResult {
        lastUpdate = command
        return writeResult
    }
    override suspend fun cambiarEstado(command: CambiarEstadoPedidoCommand): PedidoWriteResult {
        lastTransition = command
        return writeResult
    }
}

