package com.famessa.deli_antojito.data.entities

import androidx.room.Entity


@Entity(tableName = "configuracion")
data class Configuracion(
    val nombre_negocio: String
)