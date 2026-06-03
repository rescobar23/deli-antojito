package com.famessa.deli_antojito.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "configuracion")
data class Configuracion(
    @PrimaryKey
    val id: Int = com.famessa.deli_antojito.domain.model.Configuracion.SINGLETON_ID,
    @ColumnInfo(name = "nombre_negocio")
    val nombreNegocio: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
