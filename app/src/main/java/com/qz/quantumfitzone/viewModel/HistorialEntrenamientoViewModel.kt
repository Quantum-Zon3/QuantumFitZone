package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.ui.state.ActiveRoutineDashboardUiState
import com.qz.quantumfitzone.ui.state.RoutineHistoryDetailUiState
import com.qz.quantumfitzone.ui.state.RoutineHistoryExerciseItem
import com.qz.quantumfitzone.ui.state.WorkoutHistoryUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistorialEntrenamientoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val historialDao = database.historialEntrenamientoDao()
    private val ejercicioDao = database.ejercicioDao()
    private val maquinaDao = database.maquinaDao()

    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    private val _rutinaDetalleUiState = MutableStateFlow(RoutineHistoryDetailUiState())
    val rutinaDetalleUiState: StateFlow<RoutineHistoryDetailUiState> = _rutinaDetalleUiState.asStateFlow()

    private val _sesionActivaUiState = MutableStateFlow(ActiveRoutineDashboardUiState())
    val sesionActivaUiState: StateFlow<ActiveRoutineDashboardUiState> = _sesionActivaUiState.asStateFlow()

    private var currentHistoryId: Int? = null
    private var routineDetailJob: Job? = null

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
            }
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
}
