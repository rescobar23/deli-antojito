package com.famessa.deli_antojito.feature.businessconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.BusinessNameValidationResult
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.usecase.configuracion.ObserveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.SaveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.ValidateBusinessNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessConfigViewModel @Inject constructor(
    private val saveConfiguracionUseCase: SaveConfiguracionUseCase,
    private val observeConfiguracionUseCase: ObserveConfiguracionUseCase,
    private val validateBusinessNameUseCase: ValidateBusinessNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessConfigUiState())
    val uiState: StateFlow<BusinessConfigUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun start(isInitialSetup: Boolean) {
        loadJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isInitialSetup = isInitialSetup,
            isLoading = !isInitialSetup,
            isSaved = false,
            validationMessage = null,
            errorMessage = null,
            isInvalidLocalData = false
        )

        if (!isInitialSetup) {
            loadJob = viewModelScope.launch {
                observeConfiguracionUseCase()
                    .catch {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "No se pudo leer la configuracion. Intenta de nuevo."
                        )
                    }
                    .collect { configuracion ->
                        _uiState.value = _uiState.value.copy(
                            businessName = configuracion?.nombreNegocio.orEmpty(),
                            isLoading = false
                        )
                    }
            }
        }
    }

    fun onBusinessNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            businessName = value,
            validationMessage = null,
            errorMessage = null,
            isSaved = false
        )
    }

    fun retry() {
        start(_uiState.value.isInitialSetup)
    }

    fun save() {
        val current = _uiState.value
        when (validateBusinessNameUseCase(current.businessName)) {
            BusinessNameValidationResult.Blank -> {
                _uiState.value = current.copy(validationMessage = "El nombre del negocio es obligatorio.")
                return
            }
            BusinessNameValidationResult.TooLong -> {
                _uiState.value = current.copy(validationMessage = "El nombre no debe exceder 100 caracteres.")
                return
            }
            is BusinessNameValidationResult.Valid -> Unit
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                validationMessage = null,
                errorMessage = null,
                isSaved = false
            )

            val result = if (_uiState.value.isInitialSetup) {
                saveConfiguracionUseCase.saveInitial(_uiState.value.businessName)
            } else {
                saveConfiguracionUseCase.update(_uiState.value.businessName)
            }

            _uiState.value = when (result) {
                is ConfiguracionSaveResult.Success -> _uiState.value.copy(
                    businessName = result.configuracion.nombreNegocio,
                    isSaving = false,
                    isSaved = true
                )
                ConfiguracionSaveResult.InvalidName -> _uiState.value.copy(
                    isSaving = false,
                    validationMessage = "Captura un nombre de negocio valido."
                )
                ConfiguracionSaveResult.InvalidLocalData -> _uiState.value.copy(
                    isSaving = false,
                    isInvalidLocalData = true,
                    errorMessage = "La configuracion local no es valida. Revisa los datos del dispositivo."
                )
                is ConfiguracionSaveResult.PersistenceError -> _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "No se pudo guardar. Intenta de nuevo."
                )
            }
        }
    }
}
