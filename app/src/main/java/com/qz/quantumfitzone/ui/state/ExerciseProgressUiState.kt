package com.qz.quantumfitzone.ui.state

data class ProgressMetric(
    val label: String,
    val current: Double,
    val target: Double,
    val percent: Int
)

data class ProgressHistoryPoint(
    val label: String,
    val value: Float
)

data class ExerciseProgressUiState(
    val machineName: String = "",
    val weightMetric: ProgressMetric? = null,
    val repsMetric: ProgressMetric? = null,
    val setsMetric: ProgressMetric? = null,
    val weightHistory: List<ProgressHistoryPoint> = emptyList(),
    val repsHistory: List<ProgressHistoryPoint> = emptyList(),
    val setsHistory: List<ProgressHistoryPoint> = emptyList(),
    val isLoading: Boolean = true,
    val notFound: Boolean = false
)
