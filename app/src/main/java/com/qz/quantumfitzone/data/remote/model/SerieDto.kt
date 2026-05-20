package com.qz.quantumfitzone.data.remote.model

data class SerieDto(
    val idSerie: Int = 0,
    val idEjercicio: Int = 0,
    val numeroSerie: Int? = null,
    val peso: Double? = null,
    val repeticiones: Int? = null,
    val pesoObjetivo: Double? = null,
    val repeticionesObjetivo: Int? = null,
    val completada: Boolean? = false
)
