package com.famessa.deli_antojito.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Persisted order header; unique creation token protects retries and revision protects edits. */
@Entity(tableName = "pedidos", indices = [
    Index(value = ["numero"], unique = true),
    Index(value = ["create_token"], unique = true),
    Index(value = ["fecha_creacion"]),
    Index(value = ["estado", "fecha_creacion"])
])
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numero: Long,
    @ColumnInfo(name = "create_token") val createToken: String,
    val cliente: String,
    @ColumnInfo(name = "cliente_busqueda") val clienteBusqueda: String,
    @ColumnInfo(name = "numero_busqueda") val numeroBusqueda: String,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long,
    @ColumnInfo(name = "fecha_actualizacion") val fechaActualizacion: Long,
    val estado: String,
    @ColumnInfo(name = "total_centavos") val totalCentavos: Long,
    val observaciones: String?,
    val revision: Long
)
