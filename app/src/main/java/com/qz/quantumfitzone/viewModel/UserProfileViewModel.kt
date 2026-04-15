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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val personaDao = database.personaDao()
    private val historialDao = database.historialEntrenamientoDao()

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        cargarPerfilUsuarioActivo()
    }

    private fun cargarPerfilUsuarioActivo() {
        val preferences = getApplication<Application>()
            .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val correoUsuario = preferences.getString("user", "").orEmpty()

        if (correoUsuario.isBlank()) {
            _uiState.value = UserProfileUiState(
                userName = "Usuario",
                email = "",
                role = "usuario",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            historialDao.obtenerPorUsuario(correoUsuario).collectLatest { sesiones ->
                val persona = personaDao.obtenerPorCorreo(correoUsuario)
                val workouts = sesiones.count { it.completado }
                val streak = calcularRacha(sesiones)

                _uiState.value = UserProfileUiState(
                    userName = persona?.nombre?.ifBlank { "Usuario" } ?: "Usuario",
                    email = persona?.correo ?: correoUsuario,
                    role = persona?.rol ?: "usuario",
                    level = calcularNivel(workouts),
                    workouts = workouts,
                    streakDays = streak,
                    rank = calcularRango(workouts, streak),
                    isLoading = false
                )
            }
        }
    }

    private fun calcularNivel(workouts: Int): Int {
        return (workouts / 4) + 1
    }

    private fun calcularRango(workouts: Int, streak: Int): String {
        return when {
            workouts >= 30 || streak >= 21 -> "Elite"
            workouts >= 15 || streak >= 10 -> "Advanced"
            workouts >= 5 || streak >= 5 -> "Active"
            workouts >= 1 -> "Rookie"
            else -> "Beginner"
        }
    }

    private fun calcularRacha(sesiones: List<HistorialEntrenamientoEntity>): Int {
        val fechas = sesiones
            .asSequence()
            .filter { it.completado }
            .mapNotNull { parseDate(it.fecha) }
            .distinct()
            .sortedDescending()
            .toList()

        if (fechas.isEmpty()) return 0

        var streak = 1
        var referencia = fechas.first()

        for (i in 1 until fechas.size) {
            val actual = fechas[i]
            if (actual == referencia.minusDays(1)) {
                streak++
                referencia = actual
            } else if (actual != referencia) {
                break
            }
        }

        return streak
    }

    private fun parseDate(value: String): LocalDate? {
        return runCatching {
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
        }.getOrNull()
    }

    fun onNavigateToAccount() {}

    fun onNavigateToPrivacy() {}

    fun onNavigateToNotifications() {}

    fun onNavigateToConnectDevices() {}
}
