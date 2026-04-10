package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ejercicios",
    foreignKeys = [
        ForeignKey(
            entity = RutinaEntity::class,
            parentColumns = ["id_rutina"],
            childColumns = ["id_rutina"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaquinaEntity::class,
            parentColumns = ["id_maquina"],
            childColumns = ["id_maquina"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_ejercicio: Int = 0,
    val id_rutina: Int,
    val id_maquina: Int,
    val peso: Double? = null,
    val repeticiones: Int? = null,
    val series: Int? = null,
    val objetivo_peso: Double? = null,
    val objetivo_repeticiones: Int? = null,
    val objetivo_series: Int? = null,
    val fecha: String? = null
)
