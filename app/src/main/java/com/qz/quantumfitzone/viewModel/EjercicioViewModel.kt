package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.EjercicioEntity
import com.qz.quantumfitzone.ui.state.ExerciseHistoryDetailUiState
import com.qz.quantumfitzone.ui.state.ExerciseProgressUiState
import com.qz.quantumfitzone.ui.state.ProgressHistoryPoint
import com.qz.quantumfitzone.ui.state.ProgressMetric
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class EjercicioViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val ejercicioDao = database.ejercicioDao()
    private val maquinaDao = database.maquinaDao()

    private val _detalleUiState = MutableStateFlow(ExerciseHistoryDetailUiState())
    val detalleUiState: StateFlow<ExerciseHistoryDetailUiState> = _detalleUiState.asStateFlow()

    private val _progresoUiState = MutableStateFlow(ExerciseProgressUiState())
    val progresoUiState: StateFlow<ExerciseProgressUiState> = _progresoUiState.asStateFlow()

    private var currentExerciseDetailId: Int? = null
    private var currentExerciseProgressId: Int? = null
    private var progressJob: Job? = null

    // SECCION: DETALLE DE EJERCICIO

    fun loadDetalle(exerciseId: Int) {
        if (currentExerciseDetailId == exerciseId) return
        currentExerciseDetailId = exerciseId
        _detalleUiState.value = ExerciseHistoryDetailUiState(isLoading = true)

        viewModelScope.launch {
            val exercise = ejercicioDao.obtenerPorId(exerciseId)
            if (exercise == null) {
                _detalleUiState.value = ExerciseHistoryDetailUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            val machine = maquinaDao.obtenerPorId(exercise.id_maquina)
            _detalleUiState.value = ExerciseHistoryDetailUiState(
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
                repsProgress = calculateProgress(
                    exercise.repeticiones?.toDouble(),
                    exercise.objetivo_repeticiones?.toDouble()
                ),
                setsProgress = calculateProgress(
                    exercise.series?.toDouble(),
                    exercise.objetivo_series?.toDouble()
                ),
                isLoading = false,
                notFound = false
            )
        }
    }

    // SECCION: PROGRESO DE EJERCICIO

    fun loadProgreso(exerciseId: Int) {
        if (currentExerciseProgressId == exerciseId) return
        currentExerciseProgressId = exerciseId
        progressJob?.cancel()
        _progresoUiState.value = ExerciseProgressUiState(isLoading = true)

        progressJob = viewModelScope.launch {
            val exercise = ejercicioDao.obtenerPorId(exerciseId)
            if (exercise == null) {
                _progresoUiState.value = ExerciseProgressUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            val machine = maquinaDao.obtenerPorId(exercise.id_maquina)
            ejercicioDao.obtenerProgresoPorMaquina(exercise.id_maquina).collectLatest { allRecords ->
                val ordered = allRecords.sortedBy { parseDate(it.fecha) ?: LocalDate.MIN }
                _progresoUiState.value = ExerciseProgressUiState(
                    machineName = machine?.nombre ?: "Machine ${exercise.id_maquina}",
                    weightMetric = metric("Weight", exercise.peso, exercise.objetivo_peso),
                    repsMetric = metric(
                        "Repetitions",
                        exercise.repeticiones?.toDouble(),
                        exercise.objetivo_repeticiones?.toDouble()
                    ),
                    setsMetric = metric(
                        "Sets",
                        exercise.series?.toDouble(),
                        exercise.objetivo_series?.toDouble()
                    ),
                    weightHistory = toHistory(ordered) { it.peso },
                    repsHistory = toHistory(ordered) { it.repeticiones?.toDouble() },
                    setsHistory = toHistory(ordered) { it.series?.toDouble() },
                    isLoading = false,
                    notFound = false
                )
            }
        }
    }

    // SECCION: CALCULOS Y TRANSFORMACIONES

    private fun calculateProgress(current: Double?, target: Double?): Int? {
        if (current == null || target == null || target <= 0.0) return null
        return ((current / target) * 100).coerceAtMost(100.0).roundToInt()
    }

    private fun metric(label: String, current: Double?, target: Double?): ProgressMetric? {
        if (current == null || target == null || target <= 0.0) return null
        return ProgressMetric(
            label = label,
            current = current,
            target = target,
            percent = ((current / target) * 100).coerceAtMost(100.0).roundToInt()
        )
    }

    private fun toHistory(
        records: List<EjercicioEntity>,
        selector: (EjercicioEntity) -> Double?
    ): List<ProgressHistoryPoint> {
        return records.mapIndexedNotNull { index, record ->
            val value = selector(record) ?: return@mapIndexedNotNull null
            ProgressHistoryPoint(
                label = parseDate(record.fecha)?.let { formatShortDate(it) } ?: "R${index + 1}",
                value = value.toFloat()
            )
        }
    }

    private fun parseDate(value: String?): LocalDate? {
        if (value.isNullOrBlank()) return null
        return runCatching { LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
    }

    private fun formatShortDate(date: LocalDate): String {
        return "${date.monthValue}/${date.dayOfMonth}"
    }
}
