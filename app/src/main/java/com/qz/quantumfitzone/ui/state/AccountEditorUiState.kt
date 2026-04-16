package com.qz.quantumfitzone.ui.state

data class AccountEditorUiState(
    val nombre: String = "",
    val correo: String = "",
    val peso: String = "",
    val estatura: String = "",
    val password: String = "",
    val errorMessage: String? = null
)
