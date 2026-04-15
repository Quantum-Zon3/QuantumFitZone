package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_entrenamientos")
data class HistorialEntrenamientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id_historial: Int = 0,
    val correo_usuario: String,
    val fecha: String,
    val titulo: String,
    val duracion_minutos: Int,
    val kcal: Int,
    val categoria: String = "strength",
    val completado: Boolean = true
)
