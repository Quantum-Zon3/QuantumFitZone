package com.qz.quantumfitzone.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.model.PersonaEntity
import com.qz.quantumfitzone.data.repository.DatabaseProvider
import kotlinx.coroutines.launch
import kotlin.collections.forEach

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
        Onclik
    }


    // ----------------------------
    // LÓGICA DE Login
    // ----------------------------
    fun login(Onclik: () -> Unit) {
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
}