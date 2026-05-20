package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rutina_ejercicios",
    indices = [Index("id_rutina"), Index("id_exercise")]
)
data class RutinaEjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_rutina_ejercicio: Int = 0,
    val id_rutina: Int,
    val id_exercise: Int,
    val orden: Int,
    val peso_actual: Double? = null,
    val peso_objetivo: Double? = null
)
