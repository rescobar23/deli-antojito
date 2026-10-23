package com.famessa.deli_antojito.domain.model.cierre
import com.famessa.deli_antojito.domain.model.Money

/** Distinct completed orders and their historical gross amount; does not represent collections. */
data class ResumenConsultaCierre(val totalPedidos: Int, val bruto: Money)
/** Historical aggregate for one product identity. Quantity uses Long across orders. */
data class ProductoConsultaCierre(val productoId: Long, val nombreHistorico: String, val cantidad: Long, val bruto: Money)
/** One consistent query result; financial capabilities remain unavailable in this version. */
data class ConsultaCierre(val periodo: CierrePeriodo, val resumen: ResumenConsultaCierre, val topProductos: List<ProductoConsultaCierre>)
