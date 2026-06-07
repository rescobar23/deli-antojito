package com.famessa.deli_antojito.feature.businessconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

object BusinessConfigTestTags {
    const val Screen = "business_config_screen"
    const val NameInput = "business_name_input"
    const val SaveButton = "business_config_save"
    const val RetryButton = "business_config_retry"
    const val CancelButton = "business_config_cancel"
    const val Error = "business_config_error"
    const val Validation = "business_config_validation"
}

@Composable
fun BusinessConfigScreen(
    state: BusinessConfigUiState,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { contentDescription = "Pantalla de configuracion del negocio" }
            .testTag(BusinessConfigTestTags.Screen),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (state.isInitialSetup) "Configura tu negocio" else "Configuracion del negocio",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        OutlinedTextField(
            value = state.businessName,
            onValueChange = onNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Nombre del negocio" }
                .testTag(BusinessConfigTestTags.NameInput),
            label = { Text("Nombre del negocio") },
            singleLine = true,
            enabled = !state.isSaving
        )

        state.validationMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag(BusinessConfigTestTags.Validation)
            )
        }

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag(BusinessConfigTestTags.Error)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!state.isInitialSetup) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !state.isSaving,
                    modifier = Modifier.testTag(BusinessConfigTestTags.CancelButton)
                ) {
                    Text("Cancelar")
                }
            }
            Button(
                onClick = onSave,
                enabled = !state.isSaving && !state.isInvalidLocalData,
                modifier = Modifier
                    .semantics { contentDescription = "Guardar configuracion del negocio" }
                    .testTag(BusinessConfigTestTags.SaveButton)
            ) {
                Text(if (state.isSaving) "Guardando" else "Guardar")
            }
        }

        if (state.errorMessage != null || state.isInvalidLocalData) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onRetry,
                enabled = !state.isSaving,
                modifier = Modifier.testTag(BusinessConfigTestTags.RetryButton)
            ) {
                Text("Reintentar")
            }
        }
    }
}
