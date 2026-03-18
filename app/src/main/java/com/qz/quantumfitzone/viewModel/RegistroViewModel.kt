package com.qz.quantumfitzone.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.qz.quantumfitzone.clases.RegistroUsuario
import kotlin.isNaN
import kotlin.text.isBlank
import kotlin.text.trimIndent

class RegistroViewModel : ViewModel() {

    var registro by mutableStateOf(RegistroUsuario())
        private set

    // FUNCIONES DE CAMBIO DE CAMPOS



    fun onNombreChange(valor: String) {
        registro = registro. copy(nombre = valor)
    }

    fun onCorreoChange(valor: String) {
        registro = registro.copy(correo = valor)
    }

    fun onPasswordChange(valor: String) {
        registro = registro.copy(password = valor)
    }

    fun onPesochange(valor: Float) {
        registro = registro.copy(peso = valor)
    }

    fun onEstaturachange(valor: Float) {
        registro = registro.copy(estatura = valor)
    }

    fun calcularImc(peso: Float, estatura: Float){
        registro = registro.copy(imc = peso/estatura)
    }

    // ----------------------------
    // LÓGICA DE REGISTRO
    // ----------------------------
    fun registrar(){
        if (
            registro.nombre.isBlank() ||
            registro.correo.isBlank() ||
            registro.password.isBlank()
        ){
            registro = registro.copy(
            resultado = "Por favor complete todos los campos y seleccione un país."
        )
            return
        }
        if (
            registro.peso.isNaN() ||
            registro.estatura.isNaN()
        ){
            registro = registro.copy(
                resultado = "Por favor ingrese dijitos numericos en los campos correspondientes"
            )
            calcularImc(registro.peso,registro.estatura)
            return
        }
        registro = registro.copy(
            resultado = """
                DATOS REGISTRADOS:
                Nombre: ${registro.nombre}
                Correo: ${registro.correo}
                Contraseña: ${registro.password}
                Peso: ${registro.peso}
                Estatura: ${registro.estatura}
                """.trimIndent()
        )
    }
}