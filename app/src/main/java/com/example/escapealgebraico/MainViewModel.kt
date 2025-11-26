package com.example.escapealgebraico

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.escapealgebraico.utils.ProgressManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _saludoVisible = MutableStateFlow(false)
    val saludoVisible: StateFlow<Boolean> = _saludoVisible.asStateFlow()

    private val _nivelGuardado = MutableStateFlow(1)
    val nivelGuardado: StateFlow<Int> = _nivelGuardado.asStateFlow()

    private val _mostrarDialogoRetomar = MutableStateFlow(false)
    val mostrarDialogoRetomar: StateFlow<Boolean> = _mostrarDialogoRetomar.asStateFlow()

    fun onNombreChange(nuevoNombre: String) {
        _nombre.value = nuevoNombre
    }

    fun onAceptarNombre() {
        if (_nombre.value.isNotBlank()) {
            _saludoVisible.value = true
        }
    }

    fun verificarProgreso(context: Context) {
        val nivel = ProgressManager.cargarNivel(context)
        if (nivel > 1) {
            _nivelGuardado.value = nivel
            _mostrarDialogoRetomar.value = true
        }
    }

    fun ocultarDialogoRetomar() {
        _mostrarDialogoRetomar.value = false
    }
}
