package com.famessa.deli_antojito.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.usecase.configuracion.ObserveConfiguracionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeConfiguracionUseCase: ObserveConfiguracionUseCase
) : ViewModel() {

    private val _businessName = MutableStateFlow("")
    val businessName: StateFlow<String> = _businessName

    init {
        observarConfiguracion()
    }

    private fun observarConfiguracion() {
        viewModelScope.launch {
            observeConfiguracionUseCase()
                .catch { _businessName.value = "" }
                .collect { configuracion ->
                    _businessName.value = configuracion?.nombreNegocio.orEmpty()
                }
        }
    }

}
