package com.qz.quantumfitzone.data.remote.model

data class MaquinaDto(
    val idMaquina: Int = 0,
    val nombre: String,
    val grupoMuscular: String? = null,
    val descripcion: String? = null,
    val imagen: String? = null
)
