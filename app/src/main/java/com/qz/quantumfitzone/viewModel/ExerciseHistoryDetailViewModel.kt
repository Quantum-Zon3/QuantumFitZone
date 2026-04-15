package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ExerciseHistoryDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val ejercicioDao = database.ejercicioDao()
    private val maquinaDao = database.maquinaDao()

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(ExerciseHistoryDetailUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<ExerciseHistoryDetailUiState> = _uiState

    private var currentExerciseId: Int? = null

    fun load(exerciseId: Int) {
        if (currentExerciseId == exerciseId) return
        currentExerciseId = exerciseId
        _uiState.value = ExerciseHistoryDetailUiState(isLoading = true)

        viewModelScope.launch {
            val exercise = ejercicioDao.obtenerPorId(exerciseId)
            if (exercise == null) {
                _uiState.value = ExerciseHistoryDetailUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            val machine = maquinaDao.obtenerPorId(exercise.id_maquina)
            _uiState.value = ExerciseHistoryDetailUiState(
                exerciseId = exercise.id_ejercicio,
                machineId = exercise.id_maquina,
                machineName = machine?.nombre ?: "Machine ${exercise.id_maquina}",
                date = exercise.fecha.orEmpty(),
                weight = exercise.peso,
                reps = exercise.repeticiones,
                sets = exercise.series,
                targetWeight = exercise.objetivo_peso,
                targetReps = exercise.objetivo_repeticiones,
                targetSets = exercise.objetivo_series,
                weightProgress = calculateProgress(exercise.peso, exercise.objetivo_peso),
                repsProgress = calculateProgress(exercise.repeticiones?.toDouble(), exercise.objetivo_repeticiones?.toDouble()),
                setsProgress = calculateProgress(exercise.series?.toDouble(), exercise.objetivo_series?.toDouble()),
                isLoading = false,
                notFound = false
            )
        }
    }

    private fun calculateProgress(current: Double?, target: Double?): Int? {
        if (current == null || target == null || target <= 0.0) return null
        return ((current / target) * 100).coerceAtMost(100.0).roundToInt()
    }
}
