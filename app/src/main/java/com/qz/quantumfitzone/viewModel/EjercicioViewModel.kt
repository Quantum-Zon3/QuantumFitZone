package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEjercicioEntity
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
    private val historialEjercicioDao = database.historialEjercicioDao()
    private val exerciseCatalogDao = database.exerciseCatalogDao()
    private val maquinaDao = database.maquinaDao()

    private val _detalleUiState = MutableStateFlow(ExerciseHistoryDetailUiState())
    val detalleUiState: StateFlow<ExerciseHistoryDetailUiState> = _detalleUiState.asStateFlow()

    private val _progresoUiState = MutableStateFlow(ExerciseProgressUiState())
    val progresoUiState: StateFlow<ExerciseProgressUiState> = _progresoUiState.asStateFlow()

    private var currentExerciseDetailId: Int? = null
    private var currentExerciseProgressId: Int? = null
    private var progressJob: Job? = null

    // SECCION: DETALLE DE EJERCICIO

    fun loadDetalle(historyExerciseId: Int) {
        if (currentExerciseDetailId == historyExerciseId) return
        currentExerciseDetailId = historyExerciseId
        _detalleUiState.value = ExerciseHistoryDetailUiState(isLoading = true)

        viewModelScope.launch {
            val historyExercise = historialEjercicioDao.obtenerPorId(historyExerciseId)
            if (historyExercise == null) {
                _detalleUiState.value = ExerciseHistoryDetailUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            val catalogExercise = exerciseCatalogDao.obtenerPorId(historyExercise.id_exercise)
            val machine = catalogExercise?.id_maquina?.let { maquinaDao.obtenerPorId(it) }
            _detalleUiState.value = ExerciseHistoryDetailUiState(
                historyExerciseId = historyExercise.id_historial_ejercicio,
                exerciseId = historyExercise.id_exercise,
                machineId = catalogExercise?.id_maquina,
                machineName = machine?.nombre ?: historyExercise.nombre_ejercicio,
                date = historyExercise.fecha,
                weight = historyExercise.peso_realizado,
                reps = historyExercise.repeticiones_realizadas,
                sets = historyExercise.series_realizadas,
                targetWeight = historyExercise.peso_objetivo,
                targetReps = historyExercise.repeticiones_objetivo,
                targetSets = historyExercise.series_objetivo,
                weightProgress = calculateProgress(historyExercise.peso_realizado, historyExercise.peso_objetivo),
                repsProgress = calculateProgress(
                    historyExercise.repeticiones_realizadas?.toDouble(),
                    historyExercise.repeticiones_objetivo?.toDouble()
                ),
                setsProgress = calculateProgress(
                    historyExercise.series_realizadas?.toDouble(),
                    historyExercise.series_objetivo?.toDouble()
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
            val correoUsuario = getApplication<Application>()
                .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
                .getString("user", "")
                .orEmpty()

            if (correoUsuario.isBlank()) {
                _progresoUiState.value = ExerciseProgressUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            val catalogExercise = exerciseCatalogDao.obtenerPorId(exerciseId)
            val machine = catalogExercise?.id_maquina?.let { maquinaDao.obtenerPorId(it) }
            historialEjercicioDao.obtenerPorUsuarioYEjercicio(correoUsuario, exerciseId).collectLatest { allRecords ->
                if (allRecords.isEmpty()) {
                    _progresoUiState.value = ExerciseProgressUiState(
                        isLoading = false,
                        notFound = true
                    )
                    return@collectLatest
                }

                val ordered = allRecords.sortedWith(
                    compareBy(
                        { parseDate(it.fecha) ?: LocalDate.MIN },
                        { it.id_historial_ejercicio }
                    )
                )
                val latestRecord = ordered.last()
                _progresoUiState.value = ExerciseProgressUiState(
                    machineName = machine?.nombre ?: latestRecord.nombre_ejercicio,
                    weightMetric = metric("Weight", latestRecord.peso_realizado, latestRecord.peso_objetivo),
                    repsMetric = metric(
                        "Repetitions",
                        latestRecord.repeticiones_realizadas?.toDouble(),
                        latestRecord.repeticiones_objetivo?.toDouble()
                    ),
                    setsMetric = metric(
                        "Sets",
                        latestRecord.series_realizadas?.toDouble(),
                        latestRecord.series_objetivo?.toDouble()
                    ),
                    weightHistory = toHistory(ordered) { it.peso_realizado },
                    repsHistory = toHistory(ordered) { it.repeticiones_realizadas?.toDouble() },
                    setsHistory = toHistory(ordered) { it.series_realizadas?.toDouble() },
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
        records: List<HistorialEjercicioEntity>,
        selector: (HistorialEjercicioEntity) -> Double?
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
