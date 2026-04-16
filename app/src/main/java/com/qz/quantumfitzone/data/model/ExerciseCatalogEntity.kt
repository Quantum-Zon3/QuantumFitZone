package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_catalog",
    foreignKeys = [
        ForeignKey(
            entity = MaquinaEntity::class,
            parentColumns = ["id_maquina"],
            childColumns = ["id_maquina"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("id_maquina")]
)
data class ExerciseCatalogEntity(
    @PrimaryKey(autoGenerate = true)
    val id_exercise: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val grupo_muscular: String? = null,
    val id_maquina: Int? = null,
    val series: Int? = null,
    val repeticiones: Int? = null
)
