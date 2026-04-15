package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.clases.PersonaEditorState
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.data.model.PersonaEntity
import com.qz.quantumfitzone.ui.state.UserProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PersonaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val personaDao = database.personaDao()
    private val historialDao = database.historialEntrenamientoDao()

    val listaPersonas = personaDao.obtenerTodas()

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    var editorState by mutableStateOf<PersonaEditorState?>(null)
        private set

    var personaToDelete by mutableStateOf<PersonaEntity?>(null)
        private set

    var personaEntity by mutableStateOf(PersonaEntity())
        private set

    init {
        cargarPerfilUsuarioActivo()
    }

    // SECCION: CRUD DE PERSONAS

    fun obtenerTodas() {
        viewModelScope.launch {
            personaDao.obtenerTodas()
        }
    }

    suspend fun obtenerTodos(): List<PersonaEntity> {
        return personaDao.obtenerTodos()
    }

    fun obtenerPorCorreo(correo: String) {
        viewModelScope.launch {
            personaDao.obtenerPorCorreo(correo)
        }
    }

    fun insertar(persona: PersonaEntity) {
        viewModelScope.launch {
            personaDao.insertar(persona)
        }
    }

    fun actualizar(persona: PersonaEntity) {
        viewModelScope.launch {
            personaDao.actualizar(persona)
        }
    }

    fun eliminar(persona: PersonaEntity) {
        viewModelScope.launch {
            personaDao.eliminar(persona)
        }
    }

    // SECCION: ESTADO Y FILTROS DE ADMINISTRACION

    fun updateSearchQuery(value: String) {
        searchQuery = value
    }

    fun filteredPersonas(personas: List<PersonaEntity>): List<PersonaEntity> {
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) return personas

        return personas.filter { persona ->
            persona.nombre.lowercase().contains(query) ||
                persona.correo.lowercase().contains(query) ||
                persona.rol.lowercase().contains(query)
        }
    }

    fun openCreateDialog() {
        editorState = PersonaEditorState.creating()
    }

    fun openEditDialog(persona: PersonaEntity) {
        editorState = PersonaEditorState.editing(persona)
    }

    fun dismissEditorDialog() {
        editorState = null
    }

    fun requestDelete(persona: PersonaEntity) {
        personaToDelete = persona
    }

    fun dismissDeleteDialog() {
        personaToDelete = null
    }

    fun updateEditorNombre(value: String) {
        editorState = editorState?.copy(nombre = value, errorMessage = null)
    }

    fun updateEditorCorreo(value: String) {
        editorState = editorState?.let { current ->
            if (current.isEditMode) current else current.copy(correo = value, errorMessage = null)
        }
    }

    fun updateEditorPassword(value: String) {
        editorState = editorState?.copy(password = value, errorMessage = null)
    }

    fun updateEditorRol(value: String) {
        editorState = editorState?.copy(rol = value, errorMessage = null)
    }

    fun updateEditorPeso(value: String) {
        editorState = editorState?.copy(peso = value, errorMessage = null)
    }

    fun updateEditorEstatura(value: String) {
        editorState = editorState?.copy(estatura = value, errorMessage = null)
    }

    fun updateEditorEstado(value: Boolean) {
        editorState = editorState?.copy(estado = value, errorMessage = null)
    }

    fun saveEditor(): String? {
        val currentEditor = editorState ?: return "No hay un formulario activo."
        val validationError = currentEditor.validate()
        if (validationError != null) {
            editorState = currentEditor.copy(errorMessage = validationError)
            return validationError
        }

        val persona = currentEditor.toPersonaEntity()
        if (currentEditor.isEditMode) {
            actualizar(persona)
        } else {
            insertar(persona)
        }
        editorState = null
        return null
    }

    fun toggleStatus(persona: PersonaEntity) {
        actualizar(persona.copy(estado = !persona.estado))
    }

    fun confirmDelete() {
        val persona = personaToDelete ?: return
        eliminar(persona)
        personaToDelete = null
    }

    // SECCION: FORMULARIO DE REGISTRO Y LOGIN

    fun onNombreChange(valor: String) {
        personaEntity = personaEntity.copy(nombre = valor)
    }

    fun onCorreoChange(valor: String) {
        personaEntity = personaEntity.copy(correo = valor)
    }

    fun onPasswordChange(valor: String) {
        personaEntity = personaEntity.copy(password = valor)
    }

    fun onPesochange(valor: Float) {
        personaEntity = personaEntity.copy(peso = valor)
    }

    fun onEstaturachange(valor: Float) {
        personaEntity = personaEntity.copy(estatura = valor)
    }

    fun onRolChange(valor: String) {
        personaEntity = personaEntity.copy(rol = valor)
    }

    fun calcularImc(peso: Float, estatura: Float) {
        if (estatura <= 0f) return
        personaEntity = personaEntity.copy(imc = peso / (estatura * estatura))
    }

    fun registrar(onClick: () -> Unit) {
        if (
            personaEntity.nombre.isBlank() ||
            personaEntity.correo.isBlank() ||
            personaEntity.password.isBlank()
        ) {
            personaEntity = personaEntity.copy(
                resultado = "Por favor complete todos los campos."
            )
            return
        }

        if (personaEntity.peso.isNaN() || personaEntity.estatura.isNaN()) {
            personaEntity = personaEntity.copy(
                resultado = "Por favor ingrese digitos numericos en los campos correspondientes."
            )
            return
        }

        calcularImc(personaEntity.peso, personaEntity.estatura)
        insertar(personaEntity)
        personaEntity = personaEntity.copy(
            resultado = """
            DATOS REGISTRADOS:
            Nombre: ${personaEntity.nombre}
            Correo: ${personaEntity.correo}
            Contrasena: ${personaEntity.password}
            Peso: ${personaEntity.peso}
            Estatura: ${personaEntity.estatura}
        """.trimIndent()
        )
        onClick()
    }

    fun login(
        onClickDashboardUsuario: () -> Unit,
        onClickDashboardAdmin: () -> Unit,
        context: Context
    ) {
        if (personaEntity.correo.isBlank() || personaEntity.password.isBlank()) {
            personaEntity = personaEntity.copy(
                resultado = "Por favor complete todos los campos."
            )
            return
        }

        viewModelScope.launch {
            val personaEncontrada = personaDao.obtenerTodos().find {
                it.correo == personaEntity.correo && it.password == personaEntity.password
            }

            if (personaEncontrada == null) {
                personaEntity = personaEntity.copy(
                    resultado = "Correo electronico o contrasena incorrectos."
                )
                return@launch
            }

            guardarDatos(context, personaEncontrada.correo, personaEncontrada.password)
            cargarPerfilUsuarioActivo()

            if (personaEncontrada.rol == "admin") {
                onClickDashboardAdmin()
            } else {
                onClickDashboardUsuario()
            }
        }
    }

    fun guardarDatos(context: Context, usuario: String, pass: String) {
        val preferences = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        preferences.edit()
            .putString("user", usuario)
            .putString("pass", pass)
            .apply()
    }

    fun cargarDatos(context: Context, onClick: () -> Unit) {
        val preferences = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val usuario = preferences.getString("user", "")
        val pass = preferences.getString("pass", "")
        if (!usuario.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            cargarPerfilUsuarioActivo()
            onClick()
        }
    }

    // SECCION: PERFIL DEL USUARIO ACTIVO

    fun cargarPerfilUsuarioActivo() {
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

    fun onNavigateToAccount() {}

    fun onNavigateToPrivacy() {}

    fun onNavigateToNotifications() {}

    fun onNavigateToConnectDevices() {}

    // SECCION: CALCULOS DE PERFIL

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

        val hoy = LocalDate.now()
        val ultimaFecha = fechas.first()
        if (ultimaFecha != hoy && ultimaFecha != hoy.minusDays(1)) return 0

        var streak = 1
        var referencia = ultimaFecha

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
}
