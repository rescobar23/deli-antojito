package com.famessa.deli_antojito.feature.batchclosing
import com.famessa.deli_antojito.domain.model.cierre.*
import com.famessa.deli_antojito.domain.time.*

enum class BatchStatus { Loading, InvalidPeriod, Empty, Ready, Error }
data class BatchDateEditing(val date: LocalDateParts, val error: String? = null)
data class BatchClosingUiState(val periodo: CierrePeriodo? = null, val status: BatchStatus = BatchStatus.Loading,
    val consulta: ConsultaCierre? = null, val error: String? = null, val editing: BatchDateEditing? = null,
    val selectedDate: LocalDateParts? = null)
