package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "historial_ejercicios",
    indices = [
        Index("id_historial"),
        Index("id_rutina"),
        Index("id_exercise")
    ]
)
data class HistorialEjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_historial_ejercicio: Int = 0,
    val id_historial: Int,
    val id_rutina: Int,
    val id_exercise: Int,
    val correo_usuario: String,
    val fecha: String,
    val orden: Int = 0,
    val nombre_ejercicio: String,
    val series_objetivo: Int? = null,
    val repeticiones_objetivo: Int? = null,
    val peso_objetivo: Double? = null,
    val series_realizadas: Int? = null,
    val repeticiones_realizadas: Int? = null,
    val peso_realizado: Double? = null,
    val completado: Boolean = false
)
