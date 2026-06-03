package com.famessa.deli_antojito.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeView(
    onAdminClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState()

    HomeContent(
        businessName = businessName,
        onAdminClick = onAdminClick
    )
}

@Composable
fun HomeContent(
    businessName: String,
    onAdminClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (businessName.isBlank()) "Bienvenido" else businessName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("home_business_name")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAdminClick,
            modifier = Modifier.testTag("home_business_config_button")
        ) {
            Text("Configuracion del negocio")
        }
    }
}
