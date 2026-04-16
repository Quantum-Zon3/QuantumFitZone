package com.qz.quantumfitzone.ui.state

data class ActiveRoutineExercisesUiState(
    val historyId: Int? = null,
    val items: List<ActiveRoutineExerciseItemUiState> = emptyList(),
    val isLoading: Boolean = true
)

data class ActiveRoutineExerciseItemUiState(
    val historyExerciseId: Int,
    val exerciseId: Int,
    val name: String,
    val seriesObjetivo: Int? = null,
    val repeticionesObjetivo: Int? = null,
    val pesoObjetivo: Double? = null,
    val seriesRealizadasInput: String = "",
    val repeticionesRealizadasInput: String = "",
    val pesoRealizadoInput: String = "",
    val completado: Boolean = false
)
