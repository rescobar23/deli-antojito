package com.famessa.deli_antojito.feature.batchclosing
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.feature.batchclosing.components.*

@Composable fun BatchClosingScreen(state: BatchClosingUiState, onBack: ()->Unit, onEdit: ()->Unit,
    onRefresh: ()->Unit, onDismiss: ()->Unit, onApply: (LocalDateParts)->Unit) {
    Column(Modifier.fillMaxSize().aquaScreenBackground().testTag("batch_closing_screen")) {
        AquaTopBar(stringResource(R.string.batch_title),onBack,navigationIcon=OrderIcon.Back,navigationDescription="Volver a Inicio")
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            PeriodCard(state.periodo,onEdit,state.selectedDate)
            Text(stringResource(R.string.batch_notice),Modifier.testTag("batch_preliminary_notice"),style=MaterialTheme.typography.bodySmall)
            SummaryCard(if (state.status == BatchStatus.Empty || state.status == BatchStatus.Ready) state.consulta?.resumen else null)
            when(state.status) {
                BatchStatus.Loading -> CircularProgressIndicator(Modifier.testTag("batch_loading"))
                BatchStatus.InvalidPeriod, BatchStatus.Error -> Text(state.error ?: stringResource(R.string.batch_read_error),Modifier.testTag("batch_period_error"),color=MaterialTheme.colorScheme.error)
                BatchStatus.Empty, BatchStatus.Ready -> {
                    if(state.status==BatchStatus.Empty) Text(stringResource(R.string.batch_empty),Modifier.testTag("batch_empty"))
                }
            }
            UnavailablePaymentsCard()
            if (state.status == BatchStatus.Empty || state.status == BatchStatus.Ready) {
                TopProductsTable(state.consulta?.topProductos.orEmpty())
            }
            OutlinedButton(onClick=onRefresh,enabled=state.periodo!=null && state.status != BatchStatus.Loading,
                modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).testTag("batch_retry")) {
                OrderUiIcon(OrderIcon.Calendar,modifier=Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.batch_retry))
            }
        }
    }
    state.editing?.let { PeriodDateDialog(it,onDismiss,onApply) }
}

