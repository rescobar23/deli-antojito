package com.famessa.deli_antojito.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Historical line. Product id deliberately has no FK because physical catalog deletion is supported. */
@Entity(tableName = "pedido_detalle", indices = [Index(value = ["pedido_id"])], foreignKeys = [
    ForeignKey(entity = Pedido::class, parentColumns = ["id"], childColumns = ["pedido_id"],
        onDelete = ForeignKey.RESTRICT)
])
data class PedidoDetalle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "pedido_id") val pedidoId: Long,
    @ColumnInfo(name = "producto_id") val productoId: Long,
    @ColumnInfo(name = "nombre_producto") val nombreProducto: String,
    val cantidad: Int,
    @ColumnInfo(name = "precio_unitario_centavos") val precioUnitarioCentavos: Long,
    @ColumnInfo(name = "subtotal_centavos") val subtotalCentavos: Long,
    val posicion: Int
)
