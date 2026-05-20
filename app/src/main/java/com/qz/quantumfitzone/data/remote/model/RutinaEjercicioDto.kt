package com.qz.quantumfitzone.data.remote.model

data class RutinaEjercicioDto(
    val idRutinaEjercicio: Int = 0,
    val idRutina: Int,
    val idExercise: Int,
    val orden: Int,
    val pesoActual: Double? = null,
    val pesoObjetivo: Double? = null
)
