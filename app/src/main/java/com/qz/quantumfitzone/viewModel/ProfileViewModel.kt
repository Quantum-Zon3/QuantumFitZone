package com.qz.quantumfitzone.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val userName: String = "Alex Mercer",
    val level: Int = 42,
    val workouts: Int = 142,
    val streakDays: Int = 24,
    val rank: String = "Elite",
    val avatarUrl: String? = null,
    val isLoading: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onSignOut() {
        // TODO: lógica de cierre de sesión
    }

    fun onNavigateToAccount() {
        // TODO: navegación a Account settings
    }

    fun onNavigateToPrivacy() {
        // TODO: navegación a Privacy settings
    }

    fun onNavigateToNotifications() {
        // TODO: navegación a Notifications settings
    }

    fun onNavigateToConnectDevices() {
        // TODO: navegación a Connect Devices
    }
}
