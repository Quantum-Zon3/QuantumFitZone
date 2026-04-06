package com.qz.quantumfitzone.navegacion

sealed class Screen(val route: String) {
    object Welcome  : Screen("welcome")
    object Registro : Screen("registro")
    object Login    : Screen("login")

    object Dashboard : Screen("dashboard")

    object Profile   : Screen("profile")

    object MyRoutines : Screen("myRoutines")
}