package com.famessa.deli_antojito.feature.batchclosing.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.famessa.deli_antojito.domain.time.LocalDateParts
import com.famessa.deli_antojito.feature.batchclosing.BatchDateEditing
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDateDialog(edit: BatchDateEditing, dismiss: () -> Unit, apply: (LocalDateParts) -> Unit) {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(edit.date.year, edit.date.month - 1, edit.date.day)
    }
    val picker = rememberDatePickerState(initialSelectedDateMillis = utc.timeInMillis)
    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(enabled = picker.selectedDateMillis != null, onClick = {
                picker.selectedDateMillis?.let { selected ->
                    val civil = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = selected }
                    apply(LocalDateParts(civil.get(Calendar.YEAR), civil.get(Calendar.MONTH) + 1, civil.get(Calendar.DAY_OF_MONTH)))
                }
            }) { Text("Aplicar") }
        },
        dismissButton = { TextButton(onClick = dismiss) { Text("Cancelar") } }
    ) {
        DatePicker(picker, title = { Text("Fecha de consulta") }, modifier = Modifier.testTag("batch_date_picker"))
        edit.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

