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
import com.qz.quantumfitzone.data.remote.ApiClient
import com.qz.quantumfitzone.data.remote.SessionManager
import com.qz.quantumfitzone.data.remote.model.LoginBody
import com.qz.quantumfitzone.ui.state.AccountEditorUiState
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
    private val sessionManager = SessionManager(application)
    private val personaApi = ApiClient.createPersonaApi(sessionManager)

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

    var personaActual by mutableStateOf(PersonaEntity())
        private set

    var accountEditorState by mutableStateOf<AccountEditorUiState?>(null)
        private set

    var showDeleteAccountDialog by mutableStateOf(false)
        private set

    init {
        sincronizarPersonas()
        cargarPerfilUsuarioActivo()
    }

    // SECCION: CRUD DE PERSONAS

    suspend fun obtenerTodos(): List<PersonaEntity> {
        val remotas = runCatching { personaApi.obtenerPersonas() }.getOrNull()
        if (remotas != null) {
            reemplazarPersonasLocales(remotas)
            return remotas
        }
        return personaDao.obtenerTodos()
    }

    fun obtenerPorCorreo(correo: String, onResult: (PersonaEntity?) -> Unit) {
        viewModelScope.launch {
            val persona = runCatching { personaApi.obtenerPersona(correo) }
                .onSuccess { guardarPersonaLocal(it) }
                .getOrNull()
                ?: personaDao.obtenerPorCorreo(correo)
            onResult(persona)
        }
    }

    fun insertar(persona: PersonaEntity) {
        viewModelScope.launch {
            val personaGuardada = runCatching { personaApi.registrar(persona) }.getOrNull() ?: persona
            guardarPersonaLocal(personaGuardada)
        }
    }

    fun actualizar(persona: PersonaEntity) {
        viewModelScope.launch {
            val personaActualizada = runCatching {
                personaApi.actualizarPersona(persona.correo, persona)
            }.getOrNull() ?: persona
            guardarPersonaLocal(personaActualizada)
        }
    }

    fun eliminar(persona: PersonaEntity) {
        viewModelScope.launch {
            runCatching { personaApi.eliminarPersona(persona.correo) }
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
        viewModelScope.launch {
            val personaRegistrada = runCatching {
                personaApi.registrar(personaEntity)
            }.onSuccess {
                guardarPersonaLocal(it)
            }.getOrElse {
                personaEntity = personaEntity.copy(
                    resultado = "No fue posible registrar el usuario en el servidor."
                )
                return@launch
            }

            personaEntity = personaRegistrada.copy(
                resultado = """
                DATOS REGISTRADOS:
                Nombre: ${personaRegistrada.nombre}
                Correo: ${personaRegistrada.correo}
                Contrasena: ${personaRegistrada.password}
                Peso: ${personaRegistrada.peso}
                Estatura: ${personaRegistrada.estatura}
                """.trimIndent()
            )
            onClick()
        }
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
            val authResponse = runCatching {
                personaApi.login(
                    LoginBody(
                        correo = personaEntity.correo,
                        password = personaEntity.password
                    )
                )
            }.getOrNull()

            val personaEncontrada = authResponse?.persona

            if (personaEncontrada == null) {
                personaEntity = personaEntity.copy(
                    resultado = "No fue posible iniciar sesion con el servidor."
                )
                return@launch
            }

            guardarPersonaLocal(personaEncontrada)
            guardarDatos(
                context = context,
                usuario = personaEncontrada.correo,
                pass = personaEntity.password,
                accessToken = authResponse.accessToken,
                refreshToken = authResponse.refreshToken
            )
            personaActual = personaEncontrada
            cargarPerfilUsuarioActivo()

            if (personaEncontrada.rol == "admin") {
                onClickDashboardAdmin()
            } else {
                onClickDashboardUsuario()
            }
        }
    }

    fun guardarDatos(
        context: Context,
        usuario: String,
        pass: String,
        accessToken: String = sessionManager.getAccessToken(),
        refreshToken: String = sessionManager.getRefreshToken()
    ) {
        SessionManager(context).saveSession(usuario, pass, accessToken, refreshToken)
    }

    fun cargarDatos(
        context: Context,
        onClickDashboardUsuario: () -> Unit,
        onClickDashboardAdmin: () -> Unit
    ) {
        val usuario = SessionManager(context).getUser()
        val pass = SessionManager(context).getPassword()

        if (usuario.isNotEmpty() && pass.isNotEmpty()) {
            viewModelScope.launch {
                val persona = runCatching { personaApi.obtenerPersona(usuario) }
                    .onSuccess { guardarPersonaLocal(it) }
                    .getOrNull()
                    ?: personaDao.obtenerPorCorreo(usuario)

                if (persona != null && persona.password == pass) {
                    personaActual = persona
                    cargarPerfilUsuarioActivo()
                    if (persona.rol == "admin") {
                        onClickDashboardAdmin()
                    } else {
                        onClickDashboardUsuario()
                    }
                }
            }
        }
    }

    //ADMIN CREADO
    /*fun admin() {
        var personaAdmin by mutableStateOf(PersonaEntity())
        personaAdmin = personaAdmin.copy(
            nombre = "Carlitos",
            rol = "admin",
            correo = "carlitos@fitness.com",
            estado = true,
            peso = 70f,
            estatura = 1.70f,
            imc = 70f/1.70f,
            password = "Soy123"
        )
        println("PersonaAdmin: $personaAdmin")
        insertar(personaAdmin)
    }*/

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
                val persona = runCatching { personaApi.obtenerPersona(correoUsuario) }
                    .onSuccess { guardarPersonaLocal(it) }
                    .getOrNull()
                    ?: personaDao.obtenerPorCorreo(correoUsuario)
                personaActual = persona ?: PersonaEntity(correo = correoUsuario)

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

    fun usuarioActual(context: Context, onclik: () -> Unit) {
        val session = SessionManager(context)
        val usuario = session.getUser()
        val pass = session.getPassword()

        if (usuario.isNotEmpty() && pass.isNotEmpty()) {
            obtenerPorCorreo(usuario) { persona ->
                if (persona != null) {
                    personaActual = persona
                    cargarPerfilUsuarioActivo()
                } else {
                    onclik()
                }
            }
        } else {
            onclik()
        }
    }

    // SECCION: EDICION DE CUENTA PROPIA

    fun openOwnAccountEditor() {
        val correoUsuario = obtenerCorreoUsuarioActivo()
        if (correoUsuario.isBlank()) return

        viewModelScope.launch {
            val persona = personaDao.obtenerPorCorreo(correoUsuario) ?: return@launch
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
            accountEditorState = editor.copy(
                errorMessage = "Peso y estatura deben ser valores validos."
            )
            return
        }

        viewModelScope.launch {
            val personaActual = runCatching { personaApi.obtenerPersona(editor.correo) }
                .onSuccess { guardarPersonaLocal(it) }
                .getOrNull()
                ?: personaDao.obtenerPorCorreo(editor.correo)
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

            val actualizada = runCatching {
                personaApi.actualizarPersona(editor.correo, personaActualizada)
            }.getOrNull() ?: personaActualizada

            guardarPersonaLocal(actualizada)
            this@PersonaViewModel.personaActual = actualizada
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
            val persona = personaDao.obtenerPorCorreo(correoUsuario)
                ?: runCatching { personaApi.obtenerPersona(correoUsuario) }.getOrNull()
                ?: return@launch
            runCatching { personaApi.eliminarPersona(correoUsuario) }
            personaDao.eliminar(persona)
            limpiarSesion(context)
            showDeleteAccountDialog = false
            personaActual = PersonaEntity()
            _uiState.value = UserProfileUiState(isLoading = false)
            onDeleted()
        }
    }

    fun signOut(context: Context, onSignedOut: () -> Unit) {
        limpiarSesion(context)
        personaActual = PersonaEntity()
        _uiState.value = UserProfileUiState(isLoading = false)
        onSignedOut()
    }

    fun logout(context: Context, onclik: () -> Unit) {
        signOut(context, onclik)
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
        return sessionManager.getUser()
    }

    private fun limpiarSesion(context: Context) {
        SessionManager(context).clearSession()
    }

    private fun sincronizarPersonas() {
        viewModelScope.launch {
            val personas = runCatching { personaApi.obtenerPersonas() }.getOrNull() ?: return@launch
            reemplazarPersonasLocales(personas)
        }
    }

    private suspend fun guardarPersonaLocal(persona: PersonaEntity) {
        personaDao.insertar(persona)
    }

    private suspend fun reemplazarPersonasLocales(personas: List<PersonaEntity>) {
        personaDao.eliminarTodos()
        personas.forEach { personaDao.insertar(it) }
    }
}
