package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEjercicioEntity
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.data.model.RutinaEntity
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

    val routines: Flow<List<RutinaEntity>> = dao.obtenerTodas()

    var playRoutineError by mutableStateOf<String?>(null)
        private set

    fun eliminarRutina(rutina: RutinaEntity) {
        viewModelScope.launch {
            dao.eliminar(rutina)
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

            val historyId = historialDao.insertar(
                HistorialEntrenamientoEntity(
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
            ).toInt()

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

            historialEjercicioDao.insertarTodos(snapshotRows)
            onStarted()
        }
    }

    private fun obtenerCorreoUsuarioActivo(): String {
        val preferences = getApplication<Application>()
            .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        return preferences.getString("user", "").orEmpty()
    }
}
