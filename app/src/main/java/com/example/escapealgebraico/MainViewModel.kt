package com.example.escapealgebraico

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _saludoVisible = MutableStateFlow(false)
    val saludoVisible: StateFlow<Boolean> = _saludoVisible.asStateFlow()

    fun onNombreChange(nuevoNombre: String) {
        _nombre.value = nuevoNombre
    }

    fun onAceptarNombre() {
        if (_nombre.value.isNotBlank()) {
            _saludoVisible.value = true
        }
    }
}
