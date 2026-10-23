package com.famessa.deli_antojito.data.dao

import androidx.room.*
import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle
import com.famessa.deli_antojito.data.entities.PedidoTransicion
import kotlinx.coroutines.flow.Flow
import com.famessa.deli_antojito.domain.model.ResumenConteos

/** Transactional aggregate projection; lines are ordered by their persisted position when mapped. */
data class PedidoAggregate(
    @Embedded val pedido: Pedido,
    @Relation(parentColumn = "id", entityColumn = "pedido_id") val lineas: List<PedidoDetalle>
)

/** Local SQL operations; business writes must be composed in a Room transaction by the repository. */
@Dao
interface PedidoDao {
    /** Observes headers in stable creation/id order, without loading catalog images. */
    @Query("SELECT * FROM pedidos ORDER BY fecha_creacion DESC, id DESC")
    fun observeHeaders(): Flow<List<Pedido>>
    /** Observes one header and its lines consistently; absent ids emit null. */
    @Transaction @Query("SELECT * FROM pedidos WHERE id=:id")
    fun observeAggregate(id: Long): Flow<PedidoAggregate?>
    /** Observes the unfiltered order count, including cancelled records. */
    @Query("SELECT COUNT(*) FROM pedidos") fun observeCount(): Flow<Int>
    /** Applies normalized literal search, optional half-open dates, inclusive money and intersected states. */
    @Query("""SELECT * FROM pedidos WHERE
        (:text='' OR instr(cliente_busqueda, :text)>0 OR instr(numero_busqueda, :text)>0)
        AND (:allStates OR estado IN (:states))
        AND (:start IS NULL OR fecha_creacion >= :start) AND (:end IS NULL OR fecha_creacion < :end)
        AND (:min IS NULL OR total_centavos >= :min) AND (:max IS NULL OR total_centavos <= :max)
        ORDER BY fecha_creacion DESC, id DESC""")
    fun observeFiltered(text: String, allStates: Boolean, states: List<String>, start: Long?, end: Long?, min: Long?, max: Long?): Flow<List<Pedido>>
    /** Aggregates current states only for orders created in [start, end), independent of list filters. */
    @Query("""SELECT COUNT(*) AS totalPedidos,
        COALESCE(SUM(CASE WHEN estado='PENDIENTE' THEN 1 ELSE 0 END),0) AS pendientes,
        COALESCE(SUM(CASE WHEN estado='EN_PREPARACION' THEN 1 ELSE 0 END),0) AS enPreparacion,
        COALESCE(SUM(CASE WHEN estado='COMPLETADO' THEN 1 ELSE 0 END),0) AS completados
        FROM pedidos WHERE fecha_creacion >= :start AND fecha_creacion < :end""")
    fun observeSummary(start: Long, end: Long): Flow<ResumenConteos>
    /** Reads the current header for transactional revision and state validation. */
    @Query("SELECT * FROM pedidos WHERE id=:id") suspend fun get(id: Long): Pedido?
    /** Finds the first confirmed order for an idempotency token; never changes its content. */
    @Query("SELECT * FROM pedidos WHERE create_token=:token") suspend fun getByToken(token: String): Pedido?
    /** Reads historical lines without consulting products; their positions determine display order. */
    @Query("SELECT * FROM pedido_detalle WHERE pedido_id=:id ORDER BY posicion, id")
    suspend fun getLines(id: Long): List<PedidoDetalle>
    /** Reads append-only history ordered independently of wall-clock changes. */
    @Query("SELECT * FROM pedido_transiciones WHERE pedido_id=:id ORDER BY id")
    suspend fun getTransitions(id: Long): List<PedidoTransicion>
    /** Inserts a header and returns its generated id; uniqueness failures abort the enclosing transaction. */
    @Insert suspend fun insert(header: Pedido): Long
    /** Inserts all resolved lines; any constraint failure aborts the enclosing transaction. */
    @Insert suspend fun insertLines(lines: List<PedidoDetalle>)
    /** Appends the transition in the same transaction as its state update. */
    @Insert suspend fun insertTransition(transition: PedidoTransicion)
    /** Reads the singleton counter, or null if local data is invalid. */
    @Query("SELECT siguiente_numero FROM pedido_secuencia WHERE id=1") suspend fun nextNumber(): Long?
    /** Advances a validated counter inside the creation transaction; returns affected rows. */
    @Query("UPDATE pedido_secuencia SET siguiente_numero=:next WHERE id=1") suspend fun advanceNumber(next: Long): Int
    /** Replaces pending order lines only after repository-level ownership and revision checks. */
    @Query("DELETE FROM pedido_detalle WHERE pedido_id=:id") suspend fun removeLines(id: Long)
    /** Updates pending editable content at the expected revision; zero rows means a conflict. */
    @Query("""UPDATE pedidos SET cliente=:cliente, cliente_busqueda=:search, total_centavos=:total,
        observaciones=:notes, fecha_actualizacion=:now, revision=revision+1
        WHERE id=:id AND revision=:expected AND estado='PENDIENTE'""")
    suspend fun updatePending(id: Long, expected: Long, cliente: String, search: String, total: Long, notes: String?, now: Long): Int
    /** Applies an already authorized transition conditionally; zero rows means a conflict. */
    @Query("""UPDATE pedidos SET estado=:target, fecha_actualizacion=:now, revision=revision+1
        WHERE id=:id AND revision=:expected AND estado=:source""")
    suspend fun transition(id: Long, expected: Long, source: String, target: String, now: Long): Int
}
