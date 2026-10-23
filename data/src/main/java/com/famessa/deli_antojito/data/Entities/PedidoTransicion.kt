package com.famessa.deli_antojito.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Append-only transition record; cancellation keeps both order and history intact. */
@Entity(tableName = "pedido_transiciones", indices = [Index(value = ["pedido_id"])], foreignKeys = [
    ForeignKey(entity = Pedido::class, parentColumns = ["id"], childColumns = ["pedido_id"],
        onDelete = ForeignKey.RESTRICT)
])
data class PedidoTransicion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "pedido_id") val pedidoId: Long,
    @ColumnInfo(name = "estado_anterior") val estadoAnterior: String,
    @ColumnInfo(name = "estado_nuevo") val estadoNuevo: String,
    @ColumnInfo(name = "fecha_hora") val fechaHora: Long
)
