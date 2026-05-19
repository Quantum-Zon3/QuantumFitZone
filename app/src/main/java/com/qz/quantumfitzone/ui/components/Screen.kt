package com.qz.quantumfitzone.ui.components

sealed class Screen(val route: String) {
    object Welcome  : Screen("welcome")
    object Registro : Screen("registro")
    object Login    : Screen("login")

    object Dashboard : Screen("dashboard")

    object Profile   : Screen("profile")

    object MyRoutines : Screen("myRoutines")

    object EditRoutine : Screen("EditRoutine?routineId={routineId}") {
        fun createRoute(routineId: Int? = null) = if (routineId == null) {
            "EditRoutine"
        } else {
            "EditRoutine?routineId=$routineId"
        }
    }

    object SupportCenter : Screen("SupportCenter")

    object DashboardAdmin : Screen("DashboardAdmin")

    object WorkoutHistory : Screen("WorkoutHistory")

    object Progress : Screen("Progress/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "Progress/$exerciseId"
    }

    object RoutineHistoryDetail : Screen("RoutineHistoryDetail/{historyId}") {
        fun createRoute(historyId: Int) = "RoutineHistoryDetail/$historyId"
    }

    object ExerciseHistoryDetail : Screen("ExerciseHistoryDetail/{historyExerciseId}") {
        fun createRoute(historyExerciseId: Int) = "ExerciseHistoryDetail/$historyExerciseId"
    }

    object MachinesAdmin : Screen("MachinesAdmin")

    object UsersAdmin : Screen("UsersAdmin")
}
