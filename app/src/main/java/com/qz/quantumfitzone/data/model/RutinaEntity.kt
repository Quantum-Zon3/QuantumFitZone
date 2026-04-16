package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutinas")
data class RutinaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_rutina: Int = 0,
    val nombre: String,
    val categoria: String = "Strength & Conditioning",
    val descanso_segundos: Int = 60,
    val id_usuario: Int? = null
)
