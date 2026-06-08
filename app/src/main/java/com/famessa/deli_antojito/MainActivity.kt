package com.famessa.deli_antojito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.famessa.deli_antojito.core.ui.theme.Deli_antojitoTheme
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import com.famessa.deli_antojito.domain.usecase.configuracion.GetStartupConfigurationStateUseCase
import com.famessa.deli_antojito.feature.businessconfig.BusinessConfigRoute
import com.famessa.deli_antojito.feature.home.HomeView
import com.famessa.deli_antojito.feature.product.ProductEditRoute
import com.famessa.deli_antojito.feature.product.ProductListRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getStartupConfigurationStateUseCase: GetStartupConfigurationStateUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Deli_antojitoTheme(darkTheme = false) {
                val startupState by getStartupConfigurationStateUseCase()
                    .collectAsState(initial = ConfiguracionState.Loading)
                var showConfigEditor by remember { mutableStateOf(false) }
                var showProducts by remember { mutableStateOf(false) }
                var editingProductId by remember { mutableStateOf<Long?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when {
                            editingProductId != null -> ProductEditRoute(
                                productId = editingProductId,
                                onSaved = { editingProductId = null },
                                onCancel = { editingProductId = null }
                            )
                            showProducts -> ProductListRoute(
                                onAddProduct = { editingProductId = 0L },
                                onEditProduct = { editingProductId = it },
                                onBack = { showProducts = false }
                            )
                            showConfigEditor -> BusinessConfigRoute(
                                isInitialSetup = false,
                                onSaved = { showConfigEditor = false },
                                onCancel = { showConfigEditor = false }
                            )
                            startupState is ConfiguracionState.Loading -> StartupMessage(
                                message = "Cargando configuracion"
                            )
                            startupState is ConfiguracionState.MissingConfiguration -> BusinessConfigRoute(
                                isInitialSetup = true,
                                onSaved = { }
                            )
                            startupState is ConfiguracionState.Configured -> HomeView(
                                onAdminClick = { showConfigEditor = true },
                                onProductClick = { showProducts = true }
                            )
                            startupState is ConfiguracionState.InvalidLocalData -> StartupError(
                                message = "La configuracion local no es valida.",
                                onRetry = { showConfigEditor = true }
                            )
                            startupState is ConfiguracionState.PersistenceError -> StartupError(
                                message = "No se pudo leer la configuracion.",
                                onRetry = { showConfigEditor = false }
                            )
                        }
                    }
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
