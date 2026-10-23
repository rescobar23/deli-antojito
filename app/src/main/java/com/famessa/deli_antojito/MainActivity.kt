package com.famessa.deli_antojito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.famessa.deli_antojito.core.ui.theme.Deli_antojitoTheme
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import com.famessa.deli_antojito.domain.time.BusinessDayProvider
import com.famessa.deli_antojito.domain.usecase.configuracion.GetStartupConfigurationStateUseCase
import com.famessa.deli_antojito.feature.businessconfig.BusinessConfigRoute
import com.famessa.deli_antojito.navigation.MainNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var getStartupConfigurationStateUseCase: GetStartupConfigurationStateUseCase
    @Inject lateinit var businessDayProvider: BusinessDayProvider

    override fun onResume() {
        super.onResume()
        businessDayProvider.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deli_antojitoTheme(darkTheme = false) {
                // A new flow would emit Loading again and remove the navigation host.
                val startupFlow = remember(getStartupConfigurationStateUseCase) {
                    getStartupConfigurationStateUseCase()
                }
                val startupState by startupFlow.collectAsState(initial = ConfiguracionState.Loading)
                var repairConfig by remember { mutableStateOf(false) }
                when {
                    repairConfig -> BusinessConfigRoute(isInitialSetup = true, onSaved = { repairConfig = false })
                    startupState is ConfiguracionState.Loading -> StartupMessage("Cargando configuracion")
                    startupState is ConfiguracionState.MissingConfiguration -> BusinessConfigRoute(isInitialSetup = true, onSaved = {})
                    startupState is ConfiguracionState.Configured -> MainNavigation()
                    startupState is ConfiguracionState.InvalidLocalData -> StartupError("La configuracion local no es valida.") { repairConfig = true }
                    startupState is ConfiguracionState.PersistenceError -> StartupError("No se pudo leer la configuracion.") { repairConfig = true }
                }
            }
        }
    }
}

@Composable
private fun StartupMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("startup_loading"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)
    }
}

@Composable
private fun StartupError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("startup_error"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag("startup_retry")
        ) {
            Text("Reintentar")
        }
    }
}
