package com.famessa.deli_antojito.domain.model.cierre

/** Half-open creation-time interval, expressed in epoch milliseconds and an explicit business zone. */
data class CierrePeriodo(val inicioMillis: Long, val finExclusivoMillis: Long, val zonaId: String)
