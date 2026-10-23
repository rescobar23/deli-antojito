package com.famessa.deli_antojito.data.repository

import androidx.room.withTransaction
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import com.famessa.deli_antojito.data.entities.PedidoTransicion
import com.famessa.deli_antojito.data.mappers.toDomain
import com.famessa.deli_antojito.data.mappers.toSummary
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.PedidoRepository
import com.famessa.deli_antojito.domain.time.BusinessDayProvider
import com.famessa.deli_antojito.domain.usecase.pedido.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

/** Atomic local order persistence, including idempotency, optimistic revisions and historical snapshots. */
class PedidoRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val clock: BusinessDayProvider
) : PedidoRepository {
    private val dao get() = database.pedidoDao()

    private fun <T> read(source: Flow<T>): Flow<PedidoReadResult<T>> = source
        .map<T, PedidoReadResult<T>> { PedidoReadResult.Success(it) }
        .catch { failure ->
            if (failure is CancellationException) throw failure
            Timber.w("Order read failed (%s)", failure.javaClass.simpleName)
            emit(if (failure is IllegalArgumentException) PedidoReadResult.InvalidLocalData else PedidoReadResult.PersistenceError)
        }

    override fun observePedidos(consulta: PedidoConsulta): Flow<PedidoReadResult<List<PedidoResumen>>> = flow {
        require(ValidatePedidoConsultaUseCase()(consulta))
        val range = clock.range(consulta.desde, consulta.hasta)
        val states = effectivePedidoStates(consulta)
        emitAll(dao.observeFiltered(normalizePedidoSearch(consulta.texto), states == null,
            states?.map { it.name } ?: emptyList(), range.inicio, range.finExclusivo,
            consulta.minimo?.centavos, consulta.maximo?.centavos).map { rows -> rows.map { it.toSummary() } })
    }.let(::read)

    override fun observePedido(id: Long): Flow<PedidoReadResult<Pedido>> = dao.observeAggregate(id)
        .map<com.famessa.deli_antojito.data.dao.PedidoAggregate?, PedidoReadResult<Pedido>> { aggregate ->
            if (aggregate == null) PedidoReadResult.NotFound
            else PedidoReadResult.Success(aggregate.pedido.toDomain(aggregate.lineas))
        }.catch { failure ->
            if (failure is CancellationException) throw failure
            Timber.w("Order detail read failed (%s)", failure.javaClass.simpleName)
            emit(if (failure is IllegalArgumentException) PedidoReadResult.InvalidLocalData else PedidoReadResult.PersistenceError)
        }

    override fun observeTotalPedidos() = read(dao.observeCount())
    override fun observeResumen(inicio: Long, finExclusivo: Long) = read(dao.observeSummary(inicio, finExclusivo))

    private class WriteAbort(val result: PedidoWriteResult) : RuntimeException()
    private fun abort(result: PedidoWriteResult): Nothing = throw WriteAbort(result)
    private suspend fun write(block: suspend () -> PedidoWriteResult): PedidoWriteResult = try {
        database.withTransaction { block() }
    } catch (failure: CancellationException) { throw failure
    } catch (failure: WriteAbort) { failure.result
    } catch (failure: Exception) {
        Timber.w("Order write failed (%s)", failure.javaClass.simpleName)
        if (failure is IllegalArgumentException) PedidoWriteResult.InvalidLocalData else PedidoWriteResult.PersistenceError
    }

    private suspend fun aggregate(id: Long): Pedido {
        val header = dao.get(id) ?: abort(PedidoWriteResult.NotFound)
        return header.toDomain(dao.getLines(id))
    }

    private suspend fun resolve(id: Long, draft: PedidoDraft, existing: List<PedidoDetalle>): List<PedidoDetalle> {
        ValidatePedidoUseCase()(draft)?.let { abort(PedidoWriteResult.ValidationError(it)) }
        val seen = mutableSetOf<Long>()
        val resolved = draft.lineas.mapIndexed { position, input ->
            when (input) {
                is PedidoDraftLinea.Existente -> {
                    if (!seen.add(input.lineaId)) abort(PedidoWriteResult.ValidationError(PedidoValidationReason.LINEA_INVALIDA))
                    val old = existing.find { it.id == input.lineaId } ?: abort(PedidoWriteResult.ValidationError(PedidoValidationReason.LINEA_INVALIDA))
                    old.copy(cantidad = input.cantidad, subtotalCentavos = (Money(old.precioUnitarioCentavos) * input.cantidad).centavos, posicion = position)
                }
                is PedidoDraftLinea.Nueva -> {
                    val product = database.productoDao().getProductoById(input.snapshot.productoId)
                    if (product == null || !product.activo) abort(PedidoWriteResult.ProductUnavailable(input.snapshot.productoId))
                    PedidoDetalle(pedidoId = id, productoId = input.snapshot.productoId, nombreProducto = input.snapshot.nombre,
                        cantidad = input.cantidad, precioUnitarioCentavos = input.snapshot.precio.centavos,
                        subtotalCentavos = (input.snapshot.precio * input.cantidad).centavos, posicion = position)
                }
            }
        }
        // Prefer a persisted snapshot when new selections merge into an existing product/price group.
        return resolved.groupBy { it.productoId to it.precioUnitarioCentavos }.values.mapIndexed { position, group ->
            val first = group.firstOrNull { it.id > 0 } ?: group.first()
            val quantity = group.fold(0L) { count, line -> count + line.cantidad }
            require(quantity in 1..Int.MAX_VALUE.toLong())
            first.copy(cantidad = quantity.toInt(), subtotalCentavos = (Money(first.precioUnitarioCentavos) * quantity.toInt()).centavos, posicion = position)
        }
    }

    override suspend fun crear(command: CrearPedidoCommand): PedidoWriteResult = write {
        dao.getByToken(command.token)?.let { return@write PedidoWriteResult.Success(aggregate(it.id)) }
        if (command.token.isBlank() || command.draft.lineas.any { it is PedidoDraftLinea.Existente })
            abort(PedidoWriteResult.ValidationError(PedidoValidationReason.LINEA_INVALIDA))
        val draft = command.draft.normalized()
        val lines = resolve(0, draft, emptyList())
        val total = lines.fold(Money(0)) { amount, line -> amount + Money(line.subtotalCentavos) }
        val number = dao.nextNumber() ?: abort(PedidoWriteResult.InvalidLocalData)
        require(number in 1 until Long.MAX_VALUE)
        check(dao.advanceNumber(number + 1) == 1)
        val now = clock.nowMillis()
        val id = dao.insert(com.famessa.deli_antojito.data.entities.Pedido(numero = number,
            createToken = command.token, cliente = draft.cliente, clienteBusqueda = normalizePedidoSearch(draft.cliente),
            numeroBusqueda = number.toString().padStart(5, '0'), fechaCreacion = now, fechaActualizacion = now,
            estado = EstadoPedido.PENDIENTE.name, totalCentavos = total.centavos, observaciones = draft.observaciones, revision = 1))
        dao.insertLines(lines.map { it.copy(pedidoId = id) })
        PedidoWriteResult.Success(aggregate(id))
    }

    override suspend fun actualizar(command: ActualizarPedidoCommand): PedidoWriteResult = write {
        val header = dao.get(command.id) ?: abort(PedidoWriteResult.NotFound)
        header.toDomain(dao.getLines(header.id))
        if (header.revision != command.expectedRevision || header.estado != EstadoPedido.PENDIENTE.name)
            abort(PedidoWriteResult.Conflict)
        require(header.revision < Long.MAX_VALUE)
        val draft = command.draft.normalized()
        val lines = resolve(header.id, draft, dao.getLines(header.id))
        val total = lines.fold(Money(0)) { amount, line -> amount + Money(line.subtotalCentavos) }
        if (dao.updatePending(header.id, command.expectedRevision, draft.cliente, normalizePedidoSearch(draft.cliente),
                total.centavos, draft.observaciones, clock.nowMillis()) != 1) abort(PedidoWriteResult.Conflict)
        dao.removeLines(header.id)
        dao.insertLines(lines)
        PedidoWriteResult.Success(aggregate(header.id))
    }

    override suspend fun cambiarEstado(command: CambiarEstadoPedidoCommand): PedidoWriteResult = write {
        val header = dao.get(command.id) ?: abort(PedidoWriteResult.NotFound)
        header.toDomain(dao.getLines(header.id))
        if (header.revision != command.expectedRevision) abort(PedidoWriteResult.Conflict)
        val from = EstadoPedido.valueOf(header.estado)
        if (!canTransition(from, command.destino)) abort(PedidoWriteResult.InvalidTransition)
        require(header.revision < Long.MAX_VALUE)
        val now = clock.nowMillis()
        if (dao.transition(header.id, command.expectedRevision, from.name, command.destino.name, now) != 1)
            abort(PedidoWriteResult.Conflict)
        dao.insertTransition(PedidoTransicion(pedidoId = header.id, estadoAnterior = from.name, estadoNuevo = command.destino.name, fechaHora = now))
        PedidoWriteResult.Success(aggregate(header.id))
    }
}
