package com.famessa.deli_antojito.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "configuracion")
data class Configuracion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre_negocio: String
)