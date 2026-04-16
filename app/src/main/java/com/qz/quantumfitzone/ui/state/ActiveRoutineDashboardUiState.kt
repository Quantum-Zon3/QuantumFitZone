package com.qz.quantumfitzone.ui.state

data class ActiveRoutineDashboardUiState(
    val correoUsuario: String = "",
    val hasActiveSession: Boolean = false,
    val historyId: Int? = null,
    val routineTitle: String = "",
    val category: String = "",
    val date: String = "",
    val startedAt: String? = null,
    val isLoading: Boolean = true
)
