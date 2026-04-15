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
import com.qz.quantumfitzone.data.model.PersonaEntity
import kotlinx.coroutines.launch

class PersonaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseProvider
        .getDatabase(application)
        .personaDao()
    val listaPersonas = dao.obtenerTodas()

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

    var searchQuery by mutableStateOf("")
        private set

    var editorState by mutableStateOf<PersonaEditorState?>(null)
        private set

    var personaToDelete by mutableStateOf<PersonaEntity?>(null)
        private set

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


    var personaEntity by mutableStateOf(PersonaEntity())
        private set

    // FUNCIONES DE CAMBIO DE CAMPOS

    fun onNombreChange(valor: String) {
        personaEntity = personaEntity. copy(nombre = valor)
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

    fun calcularImc(peso: Float, estatura: Float){
        personaEntity = personaEntity.copy(imc = peso/estatura)
    }

    // ----------------------------
    // LÓGICA DE REGISTRO
    // ----------------------------
     fun registrar(Onclik: () -> Unit) {

        if (
            personaEntity.nombre.isBlank() ||
            personaEntity.correo.isBlank() ||
            personaEntity.password.isBlank()
        ){
            personaEntity = personaEntity.copy(
                resultado = "Por favor complete todos los campos y seleccione un país."
            )
            return
        }

        if (
            personaEntity.peso.isNaN() ||
            personaEntity.estatura.isNaN()
        ){
            personaEntity = personaEntity.copy(
                resultado = "Por favor ingrese dígitos numéricos en los campos correspondientes"
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
            Contraseña: ${personaEntity.password}
            Peso: ${personaEntity.peso}
            Estatura: ${personaEntity.estatura}
        """.trimIndent()
        )
        Onclik()
    }


    // ----------------------------
    // LÓGICA DE Login
    // ----------------------------
    fun login(Onclik: () -> Unit, context: Context) {
        if (
            personaEntity.correo.isBlank() ||
            personaEntity.password.isBlank()
        ) {
            personaEntity = personaEntity.copy(
                resultado = "Por favor complete todos los campos."
            )
            return
        }
        viewModelScope.launch {
            dao.obtenerTodas().collect { listaPersonas ->

                val personaEncontrada = listaPersonas.find {
                    it.correo == personaEntity.correo &&
                            it.password == personaEntity.password
                }

                if (personaEncontrada != null) {
                    Log.d("PERSONAS", "Se encontró: $personaEncontrada")
                    guardarDatos(context, personaEntity.correo, personaEntity.password)
                    Onclik()
                } else {
                    val error = personaEntity.copy(
                        resultado = "Correo electrónico o contraseña incorrectos."
                    )
                    Log.d("PERSONAS", error.resultado)
                }
            }
        }

    }

    fun guardarDatos(context: Context, usuario: String, pass: String) {
        println("Guardando datos en SharedPreferences: $usuario, $pass")
        val preferences = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("user", usuario)
        editor.putString("pass", pass)
        editor.apply()
    }

    fun cargarDatos(context: Context, Onclik: () -> Unit) {
        val preferences = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
        val usuario = preferences.getString("user", "")
        val pass = preferences.getString("pass", "")
        if (!usuario.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            Onclik()
        } else {
            return
        }
    }
}

data class PersonaEditorState(
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: String,
    val peso: String,
    val estatura: String,
    val estado: Boolean,
    val isEditMode: Boolean,
    val errorMessage: String? = null
) {
    fun validate(): String? {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            return "Nombre, correo y password son obligatorios."
        }

        val pesoValue = peso.toFloatOrNull()
        val estaturaValue = estatura.toFloatOrNull()

        if (pesoValue == null || estaturaValue == null) {
            return "Peso y estatura deben ser numericos."
        }

        if (estaturaValue == 0f) {
            return "La estatura no puede ser 0."
        }

        return null
    }

    fun toPersonaEntity(): PersonaEntity {
        val pesoValue = peso.toFloatOrNull() ?: 0f
        val estaturaValue = estatura.toFloatOrNull() ?: 0f
        val imcValue = if (estaturaValue > 0f) pesoValue / estaturaValue else 0f

        return PersonaEntity(
            nombre = nombre.trim(),
            correo = correo.trim(),
            password = password,
            rol = rol.ifBlank { "usuario" }.trim(),
            peso = pesoValue,
            estatura = estaturaValue,
            imc = imcValue,
            estado = estado
        )
    }

    companion object {
        fun creating(): PersonaEditorState = PersonaEditorState(
            nombre = "",
            correo = "",
            password = "",
            rol = "usuario",
            peso = "",
            estatura = "",
            estado = true,
            isEditMode = false
        )

        fun editing(persona: PersonaEntity): PersonaEditorState = PersonaEditorState(
            nombre = persona.nombre,
            correo = persona.correo,
            password = persona.password,
            rol = persona.rol,
            peso = persona.peso.toString(),
            estatura = persona.estatura.toString(),
            estado = persona.estado,
            isEditMode = true
        )
    }
}
