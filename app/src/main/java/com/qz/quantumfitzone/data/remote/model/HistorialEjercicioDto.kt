package com.qz.quantumfitzone.data.remote.model

data class HistorialEjercicioDto(
    val idHistorialEjercicio: Int = 0,
    val idHistorial: Int,
    val idRutina: Int,
    val idExercise: Int,
    val correoUsuario: String,
    val fecha: String,
    val orden: Int = 0,
    val nombreEjercicio: String,
    val seriesObjetivo: Int? = null,
    val repeticionesObjetivo: Int? = null,
    val pesoObjetivo: Double? = null,
    val seriesRealizadas: Int? = null,
    val repeticionesRealizadas: Int? = null,
    val pesoRealizado: Double? = null,
    val completado: Boolean = false
)
