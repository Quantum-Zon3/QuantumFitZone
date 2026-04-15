package com.qz.quantumfitzone.ui.state

import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity

data class WorkoutHistoryUiState(
    val correoUsuario: String = "",
    val sesiones: List<HistorialEntrenamientoEntity> = emptyList(),
    val isLoading: Boolean = true
)
