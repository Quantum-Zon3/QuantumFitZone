package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_entrenamientos")
data class HistorialEntrenamientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id_historial: Int = 0,
    val correo_usuario: String,
    val id_rutina: Int? = null,
    val fecha: String,
    val fecha_inicio: String? = null,
    val fecha_fin: String? = null,
    val titulo: String,
    val duracion_minutos: Int,
    val duracion_segundos: Int? = null,
    val kcal: Int,
    val categoria: String = "strength",
    val en_progreso: Boolean = false,
    val completado: Boolean = true
)
