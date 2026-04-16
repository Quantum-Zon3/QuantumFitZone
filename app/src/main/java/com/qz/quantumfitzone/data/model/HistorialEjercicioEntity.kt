package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "historial_ejercicios",
    foreignKeys = [
        ForeignKey(
            entity = HistorialEntrenamientoEntity::class,
            parentColumns = ["id_historial"],
            childColumns = ["id_historial"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RutinaEntity::class,
            parentColumns = ["id_rutina"],
            childColumns = ["id_rutina"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseCatalogEntity::class,
            parentColumns = ["id_exercise"],
            childColumns = ["id_exercise"],
            onDelete = ForeignKey.CASCADE
        )
    ],
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
