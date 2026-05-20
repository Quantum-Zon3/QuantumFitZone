package com.qz.quantumfitzone.data.remote.model

data class RutinaDto(
    val idRutina: Int = 0,
    val nombre: String,
    val categoria: String,
    val descansoSegundos: Int,
    val idUsuario: Int? = null
)
