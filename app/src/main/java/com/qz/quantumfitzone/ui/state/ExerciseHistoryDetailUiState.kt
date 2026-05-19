package com.qz.quantumfitzone.ui.state

data class ExerciseHistoryDetailUiState(
    val historyExerciseId: Int = 0,
    val exerciseId: Int = 0,
    val machineId: Int? = null,
    val machineName: String = "",
    val date: String = "",
    val weight: Double? = null,
    val reps: Int? = null,
    val sets: Int? = null,
    val targetWeight: Double? = null,
    val targetReps: Int? = null,
    val targetSets: Int? = null,
    val weightProgress: Int? = null,
    val repsProgress: Int? = null,
    val setsProgress: Int? = null,
    val isLoading: Boolean = true,
    val notFound: Boolean = false
)
