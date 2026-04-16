package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.ExerciseCatalogEntity
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.data.model.RutinaEntity
import com.qz.quantumfitzone.data.model.RutinaEjercicioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

data class RoutineExerciseSelection(
    val exerciseId: Int,
    val nombre: String,
    val descripcion: String?,
    val grupoMuscular: String?,
    val machineName: String?,
    val series: Int?,
    val repeticiones: Int?,
    val pesoActual: String = "",
    val pesoObjetivo: String = ""
)

class RoutineEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val routineDao = database.rutinaDao()
    private val routineExerciseDao = database.rutinaEjercicioDao()
    private val exerciseCatalogDao = database.exerciseCatalogDao()
    private val machineDao = database.maquinaDao()

    val exerciseCatalog: Flow<List<ExerciseCatalogEntity>> = exerciseCatalogDao.obtenerTodos()
    val machines: Flow<List<MaquinaEntity>> = machineDao.obtenerTodas()

    var routineName by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf(DEFAULT_CATEGORY)
        private set

    var restTimeSeconds by mutableFloatStateOf(60f)
        private set

    var routineError by mutableStateOf<String?>(null)
        private set

    var saveMessage by mutableStateOf<String?>(null)
        private set

    var currentRoutineId by mutableStateOf<Int?>(null)
        private set

    var exerciseForm by mutableStateOf(ExerciseCatalogEntity(nombre = ""))
        private set

    var isEditingExercise by mutableStateOf(false)
        private set

    var exerciseFormError by mutableStateOf<String?>(null)
        private set

    val selectedExercises = mutableStateListOf<RoutineExerciseSelection>()

    fun onRoutineNameChange(value: String) {
        routineName = value
        routineError = null
    }

    fun onCategoryChange(value: String) {
        selectedCategory = value
    }

    fun onRestTimeChange(value: Float) {
        restTimeSeconds = value
    }

    fun dismissMessage() {
        saveMessage = null
    }

    fun prepareNewRoutine() {
        clearRoutine()
    }

    fun prepareCreateExercise() {
        isEditingExercise = false
        exerciseFormError = null
        exerciseForm = ExerciseCatalogEntity(nombre = "")
    }

    fun prepareEditExercise(exercise: ExerciseCatalogEntity) {
        isEditingExercise = true
        exerciseFormError = null
        exerciseForm = exercise
    }

    fun onExerciseNameChange(value: String) {
        exerciseForm = exerciseForm.copy(nombre = value)
        exerciseFormError = null
    }

    fun onExerciseDescriptionChange(value: String) {
        exerciseForm = exerciseForm.copy(descripcion = value.ifBlank { null })
    }

    fun onExerciseGroupChange(value: String) {
        exerciseForm = exerciseForm.copy(grupo_muscular = value.ifBlank { null })
    }

    fun onExerciseMachineChange(machineId: Int?) {
        exerciseForm = exerciseForm.copy(id_maquina = machineId)
    }

    fun onExerciseSeriesChange(value: String) {
        exerciseForm = exerciseForm.copy(series = value.toIntOrNull())
    }

    fun onExerciseRepeticionesChange(value: String) {
        exerciseForm = exerciseForm.copy(repeticiones = value.toIntOrNull())
    }

    fun updateExercisePesoActual(exerciseId: Int, value: String) {
        selectedExercises.replaceAll { current ->
            if (current.exerciseId == exerciseId) current.copy(pesoActual = value) else current
        }
    }

    fun updateExercisePesoObjetivo(exerciseId: Int, value: String) {
        selectedExercises.replaceAll { current ->
            if (current.exerciseId == exerciseId) current.copy(pesoObjetivo = value) else current
        }
    }

    fun addExerciseToRoutine(
        exercise: ExerciseCatalogEntity,
        machines: List<MaquinaEntity>
    ) {
        if (selectedExercises.any { it.exerciseId == exercise.id_exercise }) return

        selectedExercises.add(
            RoutineExerciseSelection(
                exerciseId = exercise.id_exercise,
                nombre = exercise.nombre,
                descripcion = exercise.descripcion,
                grupoMuscular = exercise.grupo_muscular,
                machineName = machines.firstOrNull { it.id_maquina == exercise.id_maquina }?.nombre,
                series = exercise.series,
                repeticiones = exercise.repeticiones,
                pesoActual = "",
                pesoObjetivo = ""
            )
        )
        routineError = null
    }

    fun removeExerciseFromRoutine(exerciseId: Int) {
        selectedExercises.removeAll { it.exerciseId == exerciseId }
    }

    fun upsertExerciseCatalog(
        availableMachines: List<MaquinaEntity>,
        onSuccess: (ExerciseCatalogEntity) -> Unit
    ) {
        val cleanName = exerciseForm.nombre.trim()
        if (cleanName.isBlank()) {
            exerciseFormError = "El nombre del ejercicio es obligatorio."
            return
        }

        val cleanExercise = exerciseForm.copy(
            nombre = cleanName,
            descripcion = exerciseForm.descripcion?.trim()?.ifBlank { null },
            grupo_muscular = exerciseForm.grupo_muscular?.trim()?.ifBlank { null }
        )

        viewModelScope.launch {
            val saved = if (isEditingExercise) {
                exerciseCatalogDao.actualizar(cleanExercise)
                cleanExercise
            } else {
                val newId = exerciseCatalogDao.insertar(cleanExercise).toInt()
                cleanExercise.copy(id_exercise = newId)
            }

            selectedExercises.replaceAll { current ->
                if (current.exerciseId != saved.id_exercise) {
                    current
                } else {
                    current.copy(
                        nombre = saved.nombre,
                        descripcion = saved.descripcion,
                        grupoMuscular = saved.grupo_muscular,
                        machineName = availableMachines.firstOrNull { it.id_maquina == saved.id_maquina }?.nombre,
                        series = saved.series,
                        repeticiones = saved.repeticiones
                    )
                }
            }

            saveMessage = if (isEditingExercise) {
                "Ejercicio actualizado en el catalogo."
            } else {
                "Ejercicio creado y listo para reutilizar."
            }
            prepareCreateExercise()
            onSuccess(saved)
        }
    }

    fun deleteExerciseCatalog(exercise: ExerciseCatalogEntity) {
        viewModelScope.launch {
            exerciseCatalogDao.eliminar(exercise)
            removeExerciseFromRoutine(exercise.id_exercise)
            saveMessage = "Ejercicio eliminado del catalogo."
        }
    }

    fun saveRoutine(onSuccess: () -> Unit) {
        val cleanName = routineName.trim()
        if (cleanName.isBlank()) {
            routineError = "El nombre de la rutina es obligatorio."
            return
        }
        if (selectedExercises.isEmpty()) {
            routineError = "Selecciona al menos un ejercicio para la rutina."
            return
        }

        viewModelScope.launch {
            val isEditingRoutine = currentRoutineId != null
            val routineToSave = RutinaEntity(
                id_rutina = currentRoutineId ?: 0,
                nombre = cleanName,
                categoria = selectedCategory,
                descanso_segundos = restTimeSeconds.toInt()
            )

            val routineId = if (currentRoutineId == null) {
                routineDao.insertar(
                    routineToSave
                ).toInt()
            } else {
                routineDao.actualizar(routineToSave)
                currentRoutineId!!
            }

            routineExerciseDao.eliminarPorRutina(routineId)

            selectedExercises.forEachIndexed { index, exercise ->
                routineExerciseDao.insertar(
                    RutinaEjercicioEntity(
                        id_rutina = routineId,
                        id_exercise = exercise.exerciseId,
                        orden = index,
                        peso_actual = exercise.pesoActual.toDoubleOrNull(),
                        peso_objetivo = exercise.pesoObjetivo.toDoubleOrNull()
                    )
                )
            }

            clearRoutine()
            saveMessage = if (isEditingRoutine) {
                "Rutina actualizada correctamente."
            } else {
                "Rutina guardada correctamente."
            }
            onSuccess()
        }
    }

    fun loadRoutine(routineId: Int, machines: List<MaquinaEntity>) {
        if (currentRoutineId == routineId) return

        viewModelScope.launch {
            val routine = routineDao.obtenerPorId(routineId) ?: return@launch
            val assignments = routineExerciseDao.obtenerPorRutinaLista(routineId)

            currentRoutineId = routine.id_rutina
            routineName = routine.nombre
            selectedCategory = routine.categoria
            restTimeSeconds = routine.descanso_segundos.toFloat()
            routineError = null
            selectedExercises.clear()
            selectedExercises.addAll(
                assignments.mapNotNull { assignment ->
                    val exercise = exerciseCatalogDao.obtenerPorId(assignment.id_exercise) ?: return@mapNotNull null
                    RoutineExerciseSelection(
                        exerciseId = exercise.id_exercise,
                        nombre = exercise.nombre,
                        descripcion = exercise.descripcion,
                        grupoMuscular = exercise.grupo_muscular,
                        machineName = machines.firstOrNull { it.id_maquina == exercise.id_maquina }?.nombre,
                        series = exercise.series,
                        repeticiones = exercise.repeticiones,
                        pesoActual = assignment.peso_actual.formatWeight(),
                        pesoObjetivo = assignment.peso_objetivo.formatWeight()
                    )
                }
            )
        }
    }

    private fun clearRoutine() {
        currentRoutineId = null
        routineName = ""
        selectedCategory = DEFAULT_CATEGORY
        restTimeSeconds = 60f
        routineError = null
        selectedExercises.clear()
    }

    companion object {
        const val DEFAULT_CATEGORY = "Strength & Conditioning"
    }
}

private fun Double?.formatWeight(): String {
    if (this == null) return ""
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}
