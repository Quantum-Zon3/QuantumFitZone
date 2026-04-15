package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RoutineHistoryDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val historialDao = database.historialEntrenamientoDao()
    private val ejercicioDao = database.ejercicioDao()
    private val maquinaDao = database.maquinaDao()

    private val _uiState = MutableStateFlow(RoutineHistoryDetailUiState())
    val uiState: StateFlow<RoutineHistoryDetailUiState> = _uiState.asStateFlow()

    private var currentHistoryId: Int? = null
    private var loadJob: Job? = null

    fun load(historyId: Int) {
        if (currentHistoryId == historyId) return
        currentHistoryId = historyId
        loadJob?.cancel()
        _uiState.value = RoutineHistoryDetailUiState(isLoading = true)

        loadJob = viewModelScope.launch {
            val session = historialDao.obtenerPorId(historyId)
            if (session == null || session.id_rutina == null) {
                _uiState.value = RoutineHistoryDetailUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            ejercicioDao.obtenerPorRutinaYFecha(session.id_rutina, session.fecha).collectLatest { ejercicios ->
                val mapped = ejercicios.map { ejercicio ->
                    val maquina = maquinaDao.obtenerPorId(ejercicio.id_maquina)
                    RoutineHistoryExerciseItem(
                        exerciseId = ejercicio.id_ejercicio,
                        machineId = ejercicio.id_maquina,
                        machineName = maquina?.nombre ?: "Machine ${ejercicio.id_maquina}",
                        weight = ejercicio.peso,
                        reps = ejercicio.repeticiones,
                        sets = ejercicio.series,
                        targetWeight = ejercicio.objetivo_peso,
                        targetReps = ejercicio.objetivo_repeticiones,
                        targetSets = ejercicio.objetivo_series
                    )
                }

                _uiState.value = RoutineHistoryDetailUiState(
                    title = session.titulo,
                    date = session.fecha,
                    durationMinutes = session.duracion_minutos,
                    kcal = session.kcal,
                    exercises = mapped,
                    isLoading = false,
                    notFound = false
                )
            }
        }
    }
}
