package com.famessa.deli_antojito.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "configuracion")
data class Configuracion(
    val nombre_negocio: string
)