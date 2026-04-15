package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.data.model.PersonaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AccountEditorUiState(
    val nombre: String = "",
    val correo: String = "",
    val peso: String = "",
    val estatura: String = "",
    val password: String = "",
    val errorMessage: String? = null
)

class PersonaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseProvider.getDatabase(application)
    private val dao = database.personaDao()
    private val historialDao = database.historialEntrenamientoDao()
    val listaPersonas = dao.obtenerTodas()

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    var personaEntity by mutableStateOf(PersonaEntity())
        private set

    var accountEditorState by mutableStateOf<AccountEditorUiState?>(null)
        private set

    var showDeleteAccountDialog by mutableStateOf(false)
        private set

    init {
        cargarPerfilUsuarioActivo()
    }

    // SECCION: CRUD DE PERSONAS

    fun obtenerTodas() {
        viewModelScope.launch {
            dao.obtenerTodas()
        }
    }

    suspend fun obtenerTodos(): List<PersonaEntity> {
        return dao.obtenerTodos()
    }

    fun obtenerPorCorreo(correo: String) {
        viewModelScope.launch {
            dao.obtenerPorCorreo(correo)
        }
    }

    fun insertar(persona: PersonaEntity) {
        viewModelScope.launch {
            dao.insertar(persona)
        }
    }

    fun actualizar(persona: PersonaEntity) {
        viewModelScope.launch {
            dao.actualizar(persona)
        }
    }

    fun eliminar(persona: PersonaEntity) {
        viewModelScope.launch {
            dao.eliminar(persona)
        }
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

    fun calcularImc(peso: Float, estatura: Float) {
        if (estatura <= 0f) return
        personaEntity = personaEntity.copy(imc = peso / (estatura * estatura))
    }

    fun onRolChange(valor: String) {
        personaEntity = personaEntity.copy(rol = valor)
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
            val personaEncontrada = dao.obtenerTodos().find {
                it.correo == personaEntity.correo && it.password == personaEntity.password
            }

            if (personaEncontrada != null) {
                Log.d("PERSONAS", "Se encontro: $personaEncontrada")
                guardarDatos(context, personaEncontrada.correo, personaEncontrada.password)
                cargarPerfilUsuarioActivo()

                if (personaEncontrada.rol == "admin") {
                    onClickDashboardAdmin()
                } else {
                    onClickDashboardUsuario()
                }
            } else {
                personaEntity = personaEntity.copy(
                    resultado = "Correo electronico o contrasena incorrectos."
                )
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
        val correoUsuario = obtenerCorreoUsuarioActivo()

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
                val persona = dao.obtenerPorCorreo(correoUsuario)
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

    // SECCION: EDICION DE CUENTA PROPIA

    fun openOwnAccountEditor() {
        val correoUsuario = obtenerCorreoUsuarioActivo()
        if (correoUsuario.isBlank()) return

        viewModelScope.launch {
            val persona = dao.obtenerPorCorreo(correoUsuario) ?: return@launch
            accountEditorState = AccountEditorUiState(
                nombre = persona.nombre,
                correo = persona.correo,
                peso = if (persona.peso == 0f) "" else persona.peso.toString(),
                estatura = if (persona.estatura == 0f) "" else persona.estatura.toString(),
                password = persona.password
            )
        }
    }

    fun dismissOwnAccountEditor() {
        accountEditorState = null
    }

    fun updateOwnNombre(value: String) {
        accountEditorState = accountEditorState?.copy(nombre = value, errorMessage = null)
    }

    fun updateOwnPeso(value: String) {
        accountEditorState = accountEditorState?.copy(peso = value, errorMessage = null)
    }

    fun updateOwnEstatura(value: String) {
        accountEditorState = accountEditorState?.copy(estatura = value, errorMessage = null)
    }

    fun updateOwnPassword(value: String) {
        accountEditorState = accountEditorState?.copy(password = value, errorMessage = null)
    }

    fun saveOwnAccount(onSuccess: () -> Unit = {}) {
        val editor = accountEditorState ?: return
        val nombre = editor.nombre.trim()
        val password = editor.password.trim()
        val peso = editor.peso.toFloatOrNull()
        val estatura = editor.estatura.toFloatOrNull()

        if (nombre.isBlank() || password.isBlank()) {
            accountEditorState = editor.copy(errorMessage = "Nombre y contrasena son obligatorios.")
            return
        }

        if (peso == null || estatura == null || estatura <= 0f) {
            accountEditorState = editor.copy(errorMessage = "Peso y estatura deben ser valores validos.")
            return
        }

        viewModelScope.launch {
            val personaActual = dao.obtenerPorCorreo(editor.correo)
            if (personaActual == null) {
                accountEditorState = editor.copy(errorMessage = "No encontramos la cuenta actual.")
                return@launch
            }

            val personaActualizada = personaActual.copy(
                nombre = nombre,
                peso = peso,
                estatura = estatura,
                password = password,
                imc = peso / (estatura * estatura)
            )

            dao.actualizar(personaActualizada)
            accountEditorState = null
            cargarPerfilUsuarioActivo()
            onSuccess()
        }
    }

    // SECCION: ELIMINACION DE CUENTA Y SESION

    fun requestDeleteCurrentAccount() {
        showDeleteAccountDialog = true
    }

    fun dismissDeleteCurrentAccount() {
        showDeleteAccountDialog = false
    }

    fun deleteCurrentAccount(context: Context, onDeleted: () -> Unit) {
        val correoUsuario = obtenerCorreoUsuarioActivo()
        if (correoUsuario.isBlank()) return

        viewModelScope.launch {
            val persona = dao.obtenerPorCorreo(correoUsuario) ?: return@launch
            dao.eliminar(persona)
            limpiarSesion(context)
            showDeleteAccountDialog = false
            _uiState.value = UserProfileUiState(isLoading = false)
            onDeleted()
        }
    }

    fun signOut(context: Context, onSignedOut: () -> Unit) {
        limpiarSesion(context)
        _uiState.value = UserProfileUiState(isLoading = false)
        onSignedOut()
    }

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

    private fun obtenerCorreoUsuarioActivo(): String {
        val preferences = getApplication<Application>()
            .getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        return preferences.getString("user", "").orEmpty()
    }

    private fun limpiarSesion(context: Context) {
        val preferences = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        preferences.edit().clear().apply()
    }
}
