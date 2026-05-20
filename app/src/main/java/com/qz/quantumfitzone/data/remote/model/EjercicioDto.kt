package com.qz.quantumfitzone.data.remote.model

data class EjercicioDto(
    val idEjercicio: Int = 0,
    val idRutina: Int = 0,
    val idMaquina: Int = 0,
    val objetivoPeso: Double? = null,
    val objetivoRepeticiones: Int? = null,
    val objetivoSeries: Int? = null,
    val fecha: String? = null,
    val series: List<SerieDto>? = null
)
