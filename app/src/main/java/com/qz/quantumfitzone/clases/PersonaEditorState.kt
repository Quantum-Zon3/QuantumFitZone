package com.qz.quantumfitzone.clases

import com.qz.quantumfitzone.data.model.PersonaEntity
import java.io.Serializable

data class PersonaEditorState(
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: String,
    val peso: String,
    val estatura: String,
    val estado: Boolean,
    val isEditMode: Boolean,
    val errorMessage: String? = null
) : Serializable {
    fun validate(): String? {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            return "Nombre, correo y password son obligatorios."
        }

        val pesoValue = peso.toFloatOrNull()
        val estaturaValue = estatura.toFloatOrNull()

        if (pesoValue == null || estaturaValue == null) {
            return "Peso y estatura deben ser numericos."
        }

        if (estaturaValue == 0f) {
            return "La estatura no puede ser 0."
        }

        return null
    }

    fun toPersonaEntity(): PersonaEntity {
        val pesoValue = peso.toFloatOrNull() ?: 0f
        val estaturaValue = estatura.toFloatOrNull() ?: 0f
        val imcValue = if (estaturaValue > 0f) pesoValue / estaturaValue else 0f

        return PersonaEntity(
            nombre = nombre.trim(),
            correo = correo.trim(),
            password = password,
            rol = rol.ifBlank { "usuario" }.trim(),
            peso = pesoValue,
            estatura = estaturaValue,
            imc = imcValue,
            estado = estado
        )
    }

    companion object {
        fun creating(): PersonaEditorState = PersonaEditorState(
            nombre = "",
            correo = "",
            password = "",
            rol = "usuario",
            peso = "",
            estatura = "",
            estado = true,
            isEditMode = false
        )

        fun editing(persona: PersonaEntity): PersonaEditorState = PersonaEditorState(
            nombre = persona.nombre,
            correo = persona.correo,
            password = persona.password,
            rol = persona.rol,
            peso = persona.peso.toString(),
            estatura = persona.estatura.toString(),
            estado = persona.estado,
            isEditMode = true
        )
    }
}
