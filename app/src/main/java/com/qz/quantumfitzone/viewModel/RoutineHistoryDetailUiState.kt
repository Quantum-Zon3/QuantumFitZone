package com.qz.quantumfitzone.viewModel

data class RoutineHistoryExerciseItem(
    val exerciseId: Int,
    val machineId: Int,
    val machineName: String,
    val weight: Double?,
    val reps: Int?,
    val sets: Int?,
    val targetWeight: Double?,
    val targetReps: Int?,
    val targetSets: Int?
)

data class RoutineHistoryDetailUiState(
    val title: String = "",
    val date: String = "",
    val durationMinutes: Int = 0,
    val kcal: Int = 0,
    val exercises: List<RoutineHistoryExerciseItem> = emptyList(),
    val isLoading: Boolean = true,
    val notFound: Boolean = false
)
