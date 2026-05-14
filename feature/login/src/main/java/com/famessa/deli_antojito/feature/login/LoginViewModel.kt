package com.famessa.deli_antojito.feature.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {
    private val _usuario = mutableStateOf("")
    private val _contrasena = mutableStateOf("")
    private val _cargando = mutableStateOf(false)
    private val _inicioSesionCorrecto = mutableStateOf(false)

    val usuario: State<String> = _usuario
    val contrasena: State<String> = _contrasena
    val cargando: State<Boolean> = _cargando
    val inicioSesionCorrecto: State<Boolean> = _inicioSesionCorrecto

    fun onUsuarioChange(nuevoUsuario: String) {
        _usuario.value = nuevoUsuario
    }

    fun onContrasenaChange(nuevaContrasena: String) {
        _contrasena.value = nuevaContrasena
    }

    fun iniciarSesion() {
        _cargando.value = true
        // Aqui se mandaría la peticion para iniciar sesion a través de un Use Case
        _cargando.value = false
        _inicioSesionCorrecto.value = _usuario.value == "admin" && _contrasena.value == "admin123"
    }
}
