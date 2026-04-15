package com.qz.quantumfitzone.viewModel

data class UserProfileUiState(
    val userName: String = "Usuario",
    val email: String = "",
    val role: String = "usuario",
    val level: Int = 1,
    val workouts: Int = 0,
    val streakDays: Int = 0,
    val rank: String = "Rookie",
    val avatarUrl: String? = null,
    val isLoading: Boolean = true
)
