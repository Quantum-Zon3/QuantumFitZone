package com.qz.quantumfitzone.data.repository

import com.qz.quantumfitzone.data.model.PersonaEntity

object PersonaRepository {
    var listaPersonaEntities = mutableListOf(
        PersonaEntity("Carlos","carlitos@gmail.com" ,"123",  "usuario", 70f, 1.75f, 0f, true),
    )
}