package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.ui.state.ActiveRoutineDashboardUiState
import com.qz.quantumfitzone.ui.state.ActiveRoutineExerciseItemUiState
import com.qz.quantumfitzone.ui.state.ActiveRoutineExercisesUiState
import com.qz.quantumfitzone.ui.state.RoutineHistoryDetailUiState
import com.qz.quantumfitzone.ui.state.RoutineHistoryExerciseItem
import com.qz.quantumfitzone.ui.state.WorkoutHistoryUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistorialEntrenamientoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val historialDao = database.historialEntrenamientoDao()
    private val historialEjercicioDao = database.historialEjercicioDao()
    private val exerciseCatalogDao = database.exerciseCatalogDao()
    private val maquinaDao = database.maquinaDao()

    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    private val _rutinaDetalleUiState = MutableStateFlow(RoutineHistoryDetailUiState())
    val rutinaDetalleUiState: StateFlow<RoutineHistoryDetailUiState> = _rutinaDetalleUiState.asStateFlow()

    private val _sesionActivaUiState = MutableStateFlow(ActiveRoutineDashboardUiState())
    val sesionActivaUiState: StateFlow<ActiveRoutineDashboardUiState> = _sesionActivaUiState.asStateFlow()

    private val _sesionActivaEjerciciosUiState = MutableStateFlow(ActiveRoutineExercisesUiState())
    val sesionActivaEjerciciosUiState: StateFlow<ActiveRoutineExercisesUiState> =
        _sesionActivaEjerciciosUiState.asStateFlow()

    private var currentHistoryId: Int? = null
    private var routineDetailJob: Job? = null
    private var activeSessionExercisesJob: Job? = null

    init {
        cargarHistorialUsuarioActivo()
        cargarSesionActivaUsuario()
    }

    // SECCION: LISTA DE HISTORIAL

    fun cargarHistorialUsuarioActivo() {
        val preferences = getApplication<Application>()
            .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val correoUsuario = preferences.getString("user", "").orEmpty()

        if (correoUsuario.isBlank()) {
            _uiState.value = WorkoutHistoryUiState(
                correoUsuario = "",
                sesiones = emptyList(),
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            historialDao.obtenerPorUsuario(correoUsuario).collectLatest { sesiones ->
                _uiState.value = WorkoutHistoryUiState(
                    correoUsuario = correoUsuario,
                    sesiones = sesiones,
                    isLoading = false
                )
            }
        }
    }

    fun insertarSesion(historial: HistorialEntrenamientoEntity) {
        viewModelScope.launch {
            historialDao.insertar(historial)
        }
    }

    fun actualizarSesion(historial: HistorialEntrenamientoEntity) {
        viewModelScope.launch {
            historialDao.actualizar(historial)
        }
    }

    fun eliminarSesion(historial: HistorialEntrenamientoEntity) {
        viewModelScope.launch {
            historialDao.eliminar(historial)
        }
    }

    fun cargarSesionActivaUsuario() {
        val preferences = getApplication<Application>()
            .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val correoUsuario = preferences.getString("user", "").orEmpty()

        if (correoUsuario.isBlank()) {
            _sesionActivaUiState.value = ActiveRoutineDashboardUiState(
                correoUsuario = "",
                hasActiveSession = false,
                isLoading = false
            )
            resetSesionActivaEjercicios()
            return
        }

        viewModelScope.launch {
            historialDao.obtenerSesionActivaPorUsuario(correoUsuario).collectLatest { sesion ->
                _sesionActivaUiState.value = if (sesion == null) {
                    ActiveRoutineDashboardUiState(
                        correoUsuario = correoUsuario,
                        hasActiveSession = false,
                        isLoading = false
                    )
                } else {
                    ActiveRoutineDashboardUiState(
                        correoUsuario = correoUsuario,
                        hasActiveSession = true,
                        historyId = sesion.id_historial,
                        routineTitle = sesion.titulo,
                        category = sesion.categoria,
                        date = sesion.fecha,
                        startedAt = sesion.fecha_inicio,
                        isLoading = false
                    )
                }

                subscribeSesionActivaEjercicios(sesion?.id_historial)
            }
        }
    }

    // SECCION: EJERCICIOS DE LA SESION ACTIVA

    fun actualizarSeriesRealizadas(historyExerciseId: Int, value: String) {
        val sanitizedValue = value.filter { it.isDigit() }
        updateSesionActivaEjercicioItem(historyExerciseId) {
            it.copy(seriesRealizadasInput = sanitizedValue)
        }
    }

    fun actualizarRepeticionesRealizadas(historyExerciseId: Int, value: String) {
        val sanitizedValue = value.filter { it.isDigit() }
        updateSesionActivaEjercicioItem(historyExerciseId) {
            it.copy(repeticionesRealizadasInput = sanitizedValue)
        }
    }

    fun actualizarPesoRealizado(historyExerciseId: Int, value: String) {
        val sanitizedValue = sanitizeDecimalInput(value)
        updateSesionActivaEjercicioItem(historyExerciseId) {
            it.copy(pesoRealizadoInput = sanitizedValue)
        }
    }

    fun guardarEjercicioSesionActiva(historyExerciseId: Int) {
        persistSesionActivaEjercicio(historyExerciseId, markAsCompleted = null)
    }

    fun actualizarEstadoEjercicioSesionActiva(historyExerciseId: Int, completed: Boolean) {
        updateSesionActivaEjercicioItem(historyExerciseId) {
            it.copy(completado = completed)
        }
        persistSesionActivaEjercicio(historyExerciseId, markAsCompleted = completed)
    }

    fun finalizarSesionActiva() {
        val session = _sesionActivaUiState.value
        val exercisesState = _sesionActivaEjerciciosUiState.value

        if (!session.hasActiveSession || session.historyId == null) return
        if (exercisesState.items.isEmpty() || exercisesState.items.any { !it.completado }) return

        viewModelScope.launch {
            val historial = historialDao.obtenerPorId(session.historyId) ?: return@launch
            val endDateTime = LocalDateTime.now()
            val startDateTime = session.startedAt?.toLocalDateTimeOrNull()
            val elapsedSeconds = startDateTime?.let {
                Duration.between(it, endDateTime).seconds.coerceAtLeast(0).toInt()
            } ?: 0

            historialDao.actualizar(
                historial.copy(
                    fecha_fin = endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    duracion_segundos = elapsedSeconds,
                    duracion_minutos = (elapsedSeconds / 60).coerceAtLeast(0),
                    en_progreso = false,
                    completado = true
                )
            )
        }
    }

    // SECCION: DETALLE DE RUTINA DESDE HISTORIAL

    fun loadRutinaDetalle(historyId: Int) {
        if (currentHistoryId == historyId) return
        currentHistoryId = historyId
        routineDetailJob?.cancel()
        _rutinaDetalleUiState.value = RoutineHistoryDetailUiState(isLoading = true)

        routineDetailJob = viewModelScope.launch {
            val session = historialDao.obtenerPorId(historyId)
            if (session == null || session.id_rutina == null) {
                _rutinaDetalleUiState.value = RoutineHistoryDetailUiState(
                    isLoading = false,
                    notFound = true
                )
                return@launch
            }

            historialEjercicioDao.obtenerPorHistorial(historyId).collectLatest { ejercicios ->
                val mapped = ejercicios.map { ejercicio ->
                    val catalogExercise = exerciseCatalogDao.obtenerPorId(ejercicio.id_exercise)
                    val maquina = catalogExercise?.id_maquina?.let { maquinaDao.obtenerPorId(it) }
                    RoutineHistoryExerciseItem(
                        historyExerciseId = ejercicio.id_historial_ejercicio,
                        exerciseId = ejercicio.id_exercise,
                        machineId = catalogExercise?.id_maquina,
                        machineName = maquina?.nombre ?: ejercicio.nombre_ejercicio,
                        weight = ejercicio.peso_realizado,
                        reps = ejercicio.repeticiones_realizadas,
                        sets = ejercicio.series_realizadas,
                        targetWeight = ejercicio.peso_objetivo,
                        targetReps = ejercicio.repeticiones_objetivo,
                        targetSets = ejercicio.series_objetivo
                    )
                }

                _rutinaDetalleUiState.value = RoutineHistoryDetailUiState(
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

    private fun subscribeSesionActivaEjercicios(historyId: Int?) {
        activeSessionExercisesJob?.cancel()

        if (historyId == null) {
            resetSesionActivaEjercicios()
            return
        }

        _sesionActivaEjerciciosUiState.value = ActiveRoutineExercisesUiState(
            historyId = historyId,
            items = emptyList(),
            isLoading = true
        )

        activeSessionExercisesJob = viewModelScope.launch {
            historialEjercicioDao.obtenerPorHistorial(historyId).collectLatest { ejercicios ->
                _sesionActivaEjerciciosUiState.value = ActiveRoutineExercisesUiState(
                    historyId = historyId,
                    items = ejercicios.map { ejercicio ->
                        ActiveRoutineExerciseItemUiState(
                            historyExerciseId = ejercicio.id_historial_ejercicio,
                            exerciseId = ejercicio.id_exercise,
                            name = ejercicio.nombre_ejercicio,
                            seriesObjetivo = ejercicio.series_objetivo,
                            repeticionesObjetivo = ejercicio.repeticiones_objetivo,
                            pesoObjetivo = ejercicio.peso_objetivo,
                            seriesRealizadasInput = ejercicio.series_realizadas?.toString().orEmpty(),
                            repeticionesRealizadasInput = ejercicio.repeticiones_realizadas?.toString().orEmpty(),
                            pesoRealizadoInput = ejercicio.peso_realizado?.stripTrailingZeros().orEmpty(),
                            completado = ejercicio.completado
                        )
                    },
                    isLoading = false
                )
            }
        }
    }

    private fun updateSesionActivaEjercicioItem(
        historyExerciseId: Int,
        transform: (ActiveRoutineExerciseItemUiState) -> ActiveRoutineExerciseItemUiState
    ) {
        val currentState = _sesionActivaEjerciciosUiState.value
        _sesionActivaEjerciciosUiState.value = currentState.copy(
            items = currentState.items.map { item ->
                if (item.historyExerciseId == historyExerciseId) transform(item) else item
            }
        )
    }

    private fun persistSesionActivaEjercicio(
        historyExerciseId: Int,
        markAsCompleted: Boolean?
    ) {
        val item = _sesionActivaEjerciciosUiState.value.items.firstOrNull {
            it.historyExerciseId == historyExerciseId
        } ?: return

        viewModelScope.launch {
            val ejercicio = historialEjercicioDao.obtenerPorId(historyExerciseId) ?: return@launch
            historialEjercicioDao.actualizar(
                ejercicio.copy(
                    series_realizadas = item.seriesRealizadasInput.toIntOrNull(),
                    repeticiones_realizadas = item.repeticionesRealizadasInput.toIntOrNull(),
                    peso_realizado = item.pesoRealizadoInput.toNormalizedDoubleOrNull(),
                    completado = markAsCompleted ?: item.completado
                )
            )
        }
    }

    private fun resetSesionActivaEjercicios() {
        activeSessionExercisesJob?.cancel()
        _sesionActivaEjerciciosUiState.value = ActiveRoutineExercisesUiState(
            historyId = null,
            items = emptyList(),
            isLoading = false
        )
    }

    private fun sanitizeDecimalInput(value: String): String {
        val sanitizedChars = value.filter { it.isDigit() || it == '.' || it == ',' }
        var dotUsed = false
        return buildString {
            sanitizedChars.forEach { char ->
                if (char.isDigit()) {
                    append(char)
                } else if (!dotUsed) {
                    append('.')
                    dotUsed = true
                }
            }
        }
    }

    private fun Double.stripTrailingZeros(): String {
        return if (this % 1.0 == 0.0) {
            toInt().toString()
        } else {
            toString()
        }
    }

    private fun String.toNormalizedDoubleOrNull(): Double? {
        return replace(',', '.').toDoubleOrNull()
    }

    private fun String.toLocalDateTimeOrNull(): LocalDateTime? {
        return runCatching {
            LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }.getOrNull()
    }
}
