package com.famessa.deli_antojito.data.dao

/** Minimal LEFT JOIN row. Null line fields identify missing or corrupt historical details, never customers. */
data class CierreConsultaRow(val pedidoId: Long, val fechaCreacion: Long, val totalCentavos: Long,
    val lineaId: Long?, val productoId: Long?, val nombre: String?, val cantidad: Int?,
    val precioCentavos: Long?, val subtotalCentavos: Long?, val posicion: Int?)
