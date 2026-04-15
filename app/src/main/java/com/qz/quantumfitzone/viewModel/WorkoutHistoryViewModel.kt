package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkoutHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historialDao = DatabaseProvider
        .getDatabase(application)
        .historialEntrenamientoDao()

    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    init {
        cargarHistorialUsuarioActivo()
    }

    private fun cargarHistorialUsuarioActivo() {
        val preferences = getApplication<Application>()
            .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val correoUsuario = preferences.getString("user", "").orEmpty()

        if (correoUsuario.isBlank()) {
            _uiState.value = WorkoutHistoryUiState(
                correoUsuario = "",
                sesiones = emptyList(),
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            historialDao.obtenerPorUsuario(correoUsuario).collectLatest { sesiones ->
                _uiState.value = WorkoutHistoryUiState(
                    correoUsuario = correoUsuario,
                    sesiones = sesiones,
                    isLoading = false
                )
            }
        }
    }

    fun insertarSesion(historial: HistorialEntrenamientoEntity) {
        viewModelScope.launch {
            historialDao.insertar(historial)
        }
    }

    fun actualizarSesion(historial: HistorialEntrenamientoEntity) {
        viewModelScope.launch {
            historialDao.actualizar(historial)
        }
    }

    fun eliminarSesion(historial: HistorialEntrenamientoEntity) {
        viewModelScope.launch {
            historialDao.eliminar(historial)
        }
    }
}
