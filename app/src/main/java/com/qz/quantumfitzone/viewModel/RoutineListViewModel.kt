package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEjercicioEntity
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.data.model.RutinaEntity
import com.qz.quantumfitzone.data.remote.ApiClient
import com.qz.quantumfitzone.data.remote.SessionManager
import com.qz.quantumfitzone.data.remote.model.toDto
import com.qz.quantumfitzone.data.remote.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RoutineListViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val dao = database.rutinaDao()
    private val historialDao = database.historialEntrenamientoDao()
    private val historialEjercicioDao = database.historialEjercicioDao()
    private val rutinaEjercicioDao = database.rutinaEjercicioDao()
    private val exerciseCatalogDao = database.exerciseCatalogDao()
    private val sessionManager = SessionManager(application)
    private val rutinaApi = ApiClient.createRutinaApi(sessionManager)
    private val rutinaEjercicioApi = ApiClient.createRutinaEjercicioApi(sessionManager)
    private val historialEntrenamientoApi = ApiClient.createHistorialEntrenamientoApi(sessionManager)
    private val historialEjercicioApi = ApiClient.createHistorialEjercicioApi(sessionManager)

    val routines: Flow<List<RutinaEntity>> = dao.obtenerTodas()

    var playRoutineError by mutableStateOf<String?>(null)
        private set

    init {
        sincronizarRutinas()
    }

    fun eliminarRutina(rutina: RutinaEntity) {
        viewModelScope.launch {
            runCatching {
                rutinaEjercicioApi.obtenerRutinaEjerciciosPorRutina(rutina.id_rutina)
            }.getOrNull().orEmpty().forEach { assignment ->
                runCatching { rutinaEjercicioApi.eliminarRutinaEjercicio(assignment.idRutinaEjercicio) }
            }
            runCatching { rutinaApi.eliminarRutina(rutina.id_rutina) }
            dao.eliminar(rutina)
            rutinaEjercicioDao.eliminarPorRutina(rutina.id_rutina)
        }
    }

    fun dismissPlayRoutineError() {
        playRoutineError = null
    }

    fun startRoutineSession(routineId: Int, onStarted: () -> Unit) {
        viewModelScope.launch {
            val correoUsuario = obtenerCorreoUsuarioActivo()
            if (correoUsuario.isBlank()) {
                playRoutineError = "Debes iniciar sesion para comenzar una rutina."
                return@launch
            }

            val activeSession = historialDao.obtenerSesionActivaActual(correoUsuario)
            if (activeSession != null) {
                onStarted()
                return@launch
            }

            val routine = dao.obtenerPorId(routineId)
            if (routine == null) {
                playRoutineError = "No encontramos la rutina seleccionada."
                return@launch
            }

            val assignments = rutinaEjercicioDao.obtenerPorRutinaLista(routineId)
            if (assignments.isEmpty()) {
                playRoutineError = "La rutina no tiene ejercicios para iniciar."
                return@launch
            }

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val startTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val historyPayload = HistorialEntrenamientoEntity(
                correo_usuario = correoUsuario,
                id_rutina = routine.id_rutina,
                fecha = today,
                fecha_inicio = startTimestamp,
                titulo = routine.nombre,
                duracion_minutos = 0,
                duracion_segundos = 0,
                kcal = 0,
                categoria = routine.categoria,
                en_progreso = true,
                completado = false
            )

            val remoteHistory = runCatching {
                historialEntrenamientoApi.crearHistorialEntrenamiento(historyPayload.toDto()).toEntity()
            }.getOrNull()

            val savedHistory = guardarHistorialLocal(
                (remoteHistory ?: historyPayload).copy(id_rutina = routine.id_rutina)
            )
            val historyId = savedHistory.id_historial

            val snapshotRows = assignments.mapNotNull { assignment ->
                val exercise = exerciseCatalogDao.obtenerPorId(assignment.id_exercise) ?: return@mapNotNull null
                HistorialEjercicioEntity(
                    id_historial = historyId,
                    id_rutina = routine.id_rutina,
                    id_exercise = exercise.id_exercise,
                    correo_usuario = correoUsuario,
                    fecha = today,
                    orden = assignment.orden,
                    nombre_ejercicio = exercise.nombre,
                    series_objetivo = exercise.series,
                    repeticiones_objetivo = exercise.repeticiones,
                    peso_objetivo = assignment.peso_objetivo
                )
            }

            if (snapshotRows.isEmpty()) {
                playRoutineError = "No pudimos preparar los ejercicios de la rutina."
                return@launch
            }

            snapshotRows.forEach { snapshot ->
                val remoteSnapshot = runCatching {
                    historialEjercicioApi.crearHistorialEjercicio(snapshot.toDto()).toEntity()
                }.getOrNull()

                val localSnapshot = (remoteSnapshot ?: snapshot).copy(
                    id_historial_ejercicio = remoteSnapshot
                        ?.id_historial_ejercicio
                        ?.takeIf { it > 0 }
                        ?: 0,
                    id_historial = historyId,
                    id_rutina = routine.id_rutina,
                    id_exercise = snapshot.id_exercise
                )
                historialEjercicioDao.insertar(localSnapshot)
            }
            onStarted()
        }
    }

    private fun obtenerCorreoUsuarioActivo(): String {
        return sessionManager.getUser()
    }

    private fun sincronizarRutinas() {
        viewModelScope.launch {
            val rutinasRemotas = runCatching { rutinaApi.obtenerRutinas() }.getOrNull() ?: return@launch
            rutinaEjercicioDao.eliminarTodos()
            dao.eliminarTodas()
            rutinasRemotas.forEach { rutinaDto ->
                val rutina = rutinaDto.toEntity()
                dao.insertar(rutina)
                val assignments = runCatching {
                    rutinaEjercicioApi.obtenerRutinaEjerciciosPorRutina(rutina.id_rutina)
                }.getOrNull().orEmpty()
                assignments.forEach { rutinaEjercicioDao.insertar(it.toEntity()) }
            }
        }
    }

    private suspend fun guardarHistorialLocal(
        historial: HistorialEntrenamientoEntity
    ): HistorialEntrenamientoEntity {
        if (historial.id_historial > 0) {
            historialDao.insertar(historial)
            return historial
        }

        val localId = historialDao.insertar(historial.copy(id_historial = 0)).toInt()
        return historial.copy(id_historial = localId)
    }
}
