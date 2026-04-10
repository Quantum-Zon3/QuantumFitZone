package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maquinas")
data class MaquinaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_maquina: Int = 0,
    val nombre: String,
    val grupo_muscular: String? = null,
    val descripcion: String? = null,
    val imagen: String? = null
)
