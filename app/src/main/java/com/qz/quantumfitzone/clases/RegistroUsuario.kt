package com.qz.quantumfitzone.clases

import java.io.Serializable

data class RegistroUsuario(
    val nombre: String = "",
    val correo: String = "",
    val password: String = "",
    val rol: String = "usuario",
    val peso: Float = 0f,
    val estatura: Float = 0f,
    val imc: Float = 0f,
    val estado: Boolean = true,
    val resultado: String = "",
    ) : Serializable
