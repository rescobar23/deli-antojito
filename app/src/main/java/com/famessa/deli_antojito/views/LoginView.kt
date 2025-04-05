package com.famessa.deli_antojito.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.viewModels.LoginViewModel


@Composable
fun LoginView(viewModel: LoginViewModel) {

    val usuario by viewModel.usuario
    val contrasena by viewModel.contrasena
    val cargando by viewModel.cargando
    val inicioSesionCorrecto by viewModel.inicioSesionCorrecto
    val ctx = LocalContext.current


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier=Modifier.weight(1f))
        OutlinedTextField(
            value = usuario,
            label = { Text("Usuario") },
            onValueChange = viewModel::onUsuarioChange,
            enabled = !cargando
        )
        OutlinedTextField(value = contrasena,
            label = { Text("Contraseña")},
            onValueChange = viewModel::onContrasenaChange,
            enabled = !cargando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier=Modifier.weight(1f))
        Button(
            onClick = {
                var msg = ""
                viewModel.iniciarSesion()
                if(inicioSesionCorrecto){
                    msg = "Inicio de sesión correcto con las credenciales [${usuario},${contrasena}]"
                } else {
                    msg = "Inicio de sesión incorrecto con las credenciales [${usuario},${contrasena}]"
                }
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Iniciar Sesión")
        }
    }
}