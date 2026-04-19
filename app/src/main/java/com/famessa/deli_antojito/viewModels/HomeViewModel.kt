package com.famessa.deli_antojito.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.data.Entities.Producto
import com.famessa.deli_antojito.data.Repository.ProductoRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ProductoRepositoryInterface) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _nombre = mutableStateOf("")
    private val _precioBase = mutableStateOf("")
    private val _categoria = mutableStateOf("")
    private val _mensaje = mutableStateOf("")

    val nombre: State<String> = _nombre
    val precioBase: State<String> = _precioBase
    val categoria: State<String> = _categoria
    val mensaje: State<String> = _mensaje

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllProductos().collect { productos ->
                _productos.value = productos
                _isLoading.value = false
            }
        }
    }

    fun onNombreChange(nuevoNombre: String) {
        _nombre.value = nuevoNombre
    }

    fun onPrecioBaseChange(nuevoPrecio: String) {
        _precioBase.value = nuevoPrecio
    }

    fun onCategoriaChange(nuevaCategoria: String) {
        _categoria.value = nuevaCategoria
    }

    fun guardarProducto() {
        viewModelScope.launch {
            try {
                val precio = _precioBase.value.toDoubleOrNull()
                if (precio == null) {
                    _mensaje.value = "Precio base debe ser un número válido"
                    return@launch
                }

                if (_nombre.value.isBlank() || _categoria.value.isBlank()) {
                    _mensaje.value = "Nombre y categoría son obligatorios"
                    return@launch
                }

                val producto = Producto(
                    nombre = _nombre.value,
                    precioBase = precio,
                    categoria = _categoria.value
                )

                repository.insertProducto(producto)
                _mensaje.value = "Producto guardado exitosamente"

                // Limpiar campos
                _nombre.value = ""
                _precioBase.value = ""
                _categoria.value = ""

                // Recargar productos
                cargarProductos()

            } catch (e: Exception) {
                _mensaje.value = "Error al guardar producto: ${e.message}"
            }
        }
    }

    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteProducto(id)
                _mensaje.value = "Producto eliminado exitosamente"
                cargarProductos()
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar producto: ${e.message}"
            }
        }
    }
}