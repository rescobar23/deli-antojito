package com.famessa.deli_antojito.feature.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginView(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val usuario by viewModel.usuario
    val contrasena by viewModel.contrasena
    val cargando by viewModel.cargando
    val inicioSesionCorrecto by viewModel.inicioSesionCorrecto
    val ctx = LocalContext.current

    LaunchedEffect(inicioSesionCorrecto) {
        if (inicioSesionCorrecto) {
            onLoginSuccess()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier=Modifier.weight(1f))
        OutlinedTextField(
            value = usuario,
            label = { Text("Usuario") },
            onValueChange = viewModel::onUsuarioChange,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = contrasena,
            label = { Text("Contraseña")},
            onValueChange = viewModel::onContrasenaChange,
            enabled = !cargando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier=Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.iniciarSesion()
                val msg = if(inicioSesionCorrecto){
                    "Inicio de sesión correcto"
                } else {
                    "Inicio de sesión incorrecto"
                }
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Iniciar Sesión")
        }
    }
}
