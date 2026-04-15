package com.qz.quantumfitzone.ui.components

sealed class Screen(val route: String) {
    object Welcome  : Screen("welcome")
    object Registro : Screen("registro")
    object Login    : Screen("login")

    object Dashboard : Screen("dashboard")

    object Profile   : Screen("profile")

    object MyRoutines : Screen("myRoutines")

    object EditRoutine : Screen("EditRoutine")

    object SupportCenter : Screen("SupportCenter")

    object DashboardAdmin : Screen("DashboardAdmin")

    object WorkoutHistory : Screen("WorkoutHistory")

    object Progress : Screen("Progress")

    object MachinesAdmin : Screen("MachinesAdmin")

    object UsersAdmin : Screen("UsersAdmin")
}
