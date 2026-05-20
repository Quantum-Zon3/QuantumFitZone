package com.qz.quantumfitzone.data.remote.model

data class ExerciseCatalogDto(
    val idExercise: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val grupoMuscular: String? = null,
    val idMaquina: Int? = null,
    val series: Int? = null,
    val repeticiones: Int? = null
)
