package com.qz.quantumfitzone.data.remote.model

import com.qz.quantumfitzone.data.model.PersonaEntity

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val persona: PersonaEntity
)
