package com.qz.quantumfitzone.data.remote.model

data class HistorialEntrenamientoDto(
    val idHistorial: Int = 0,
    val correoUsuario: String,
    val idRutina: Int? = null,
    val fecha: String,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val titulo: String,
    val duracionMinutos: Int,
    val duracionSegundos: Int? = null,
    val kcal: Int,
    val categoria: String = "strength",
    val enProgreso: Boolean = false,
    val completado: Boolean = true
)
