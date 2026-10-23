package com.famessa.deli_antojito.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Singleton sequence, advanced only in the transaction committing a new order. */
@Entity(tableName = "pedido_secuencia")
data class PedidoSecuencia(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "siguiente_numero") val siguienteNumero: Long = 1
)
