package com.qz.quantumfitzone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class PersonaEntity(

    val nombre: String = "",
    @PrimaryKey
    val correo: String = "",
    val password: String = "",
    val rol: String = "usuario",
    val peso: Float = 0f,
    val estatura: Float = 0f,
    val imc: Float = 0f,
    val estado: Boolean = true,
    val resultado: String = "",
    )
