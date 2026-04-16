package com.qz.quantumfitzone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quantumfitzone.QuantumFitzoneScreen
import com.qz.quantumfitzone.SupportScreen
import com.qz.quantumfitzone.ui.components.Screen
import com.qz.quantumfitzone.ui.screens.AdminUsersScreen
import com.qz.quantumfitzone.ui.screens.BottomNavDestination
import com.qz.quantumfitzone.ui.screens.DashboardScreen
import com.qz.quantumfitzone.ui.screens.EditRoutineScreen
import com.qz.quantumfitzone.ui.screens.ExerciseHistoryDetailScreen
import com.qz.quantumfitzone.ui.screens.LoginForm
import com.qz.quantumfitzone.ui.screens.MyRoutinesScreen
import com.qz.quantumfitzone.ui.screens.ProfileScreen
import com.qz.quantumfitzone.ui.screens.ProgressScreen
import com.qz.quantumfitzone.ui.screens.RegistroUsuarioForm
import com.qz.quantumfitzone.ui.screens.RoutineHistoryDetailScreen
import com.qz.quantumfitzone.ui.screens.DashboardScreenAdmin
import com.qz.quantumfitzone.ui.screens.WorkoutHistoryScreen
import com.qz.quantumfitzone.ui.screens.AdminMachinesScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            QuantumFitzoneScreen(
                onRegistro = { navController.navigate(Screen.Registro.route) },
                onLogin = { navController.navigate(Screen.Login.route) },
                onBack = { navController.popBackStack() },
                onDashboard = { navController.navigate(Screen.Dashboard.route) },
                onDashboardAdmin = { navController.navigate(Screen.DashboardAdmin.route) }
            )
        }

        composable(Screen.Registro.route) {
            RegistroUsuarioForm(
                onBack = { navController.popBackStack() },
                onLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginForm(
                onBack = { navController.popBackStack() },
                onLogin = { navController.navigate(Screen.Dashboard.route) },
                onForgotPassword = { },
                onRegister = { navController.navigate(Screen.Registro.route) },
                onDashboardAdmin = { navController.navigate(Screen.DashboardAdmin.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.SUPPORT -> navController.navigate(Screen.SupportCenter.route)
                        BottomNavDestination.HISTORY -> navController.navigate(Screen.WorkoutHistory.route)
                        BottomNavDestination.PROGRESS -> { }
                        BottomNavDestination.PROFILE -> { }
                        else -> {}
                    }
                }
            )
        }

        composable(Screen.MyRoutines.route) {
            MyRoutinesScreen(
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.SUPPORT -> navController.navigate(Screen.SupportCenter.route)
                        BottomNavDestination.HISTORY -> navController.navigate(Screen.WorkoutHistory.route)
                        BottomNavDestination.PROGRESS -> { }
                        BottomNavDestination.ROUTINES -> { }
                        else -> {}
                    }
                },
                onEditRoutine = { navController.navigate(Screen.EditRoutine.route) }
            )
        }

        composable(Screen.EditRoutine.route) {
            EditRoutineScreen(
                onNavigateBack = { navController.navigate(Screen.MyRoutines.route) },
                onListo = {
                    navController.navigate(Screen.MyRoutines.route) {
                        popUpTo(Screen.MyRoutines.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.DASHBOARD -> { }
                        BottomNavDestination.PROGRESS -> { }
                        BottomNavDestination.HISTORY -> navController.navigate(Screen.WorkoutHistory.route)
                        BottomNavDestination.SUPPORT -> navController.navigate(Screen.SupportCenter.route)
                        else -> {}
                    }
                }
            )
        }

        composable(Screen.SupportCenter.route) {
            SupportScreen(
                onBack = { navController.navigate(Screen.Dashboard.route) },
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.SUPPORT -> { }
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.HISTORY -> navController.navigate(Screen.WorkoutHistory.route)
                        BottomNavDestination.PROGRESS -> { }
                        else -> {}
                    }
                }
            )
        }

        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.SUPPORT -> navController.navigate(Screen.SupportCenter.route)
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.PROGRESS -> { }
                        BottomNavDestination.HISTORY -> {navController.navigate(Screen.WorkoutHistory.route) }
                        else -> {}
                    }
                },
                onOpenRoutine = { historyId ->
                    navController.navigate(Screen.RoutineHistoryDetail.createRoute(historyId))
                }
            )
        }

        composable(
            route = Screen.RoutineHistoryDetail.route,
            arguments = listOf(navArgument("historyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val historyId = backStackEntry.arguments?.getInt("historyId") ?: return@composable
            RoutineHistoryDetailScreen(
                historyId = historyId,
                onNavigateBack = { navController.popBackStack() },
                onOpenExercise = { exerciseId ->
                    navController.navigate(Screen.ExerciseHistoryDetail.createRoute(exerciseId))
                }
            )
        }

        composable(
            route = Screen.ExerciseHistoryDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: return@composable
            ExerciseHistoryDetailScreen(
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() },
                onOpenProgress = { selectedExerciseId ->
                    navController.navigate(Screen.Progress.createRoute(selectedExerciseId))
                }
            )
        }

        composable(
            route = Screen.Progress.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: return@composable
            ProgressScreen(
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DashboardAdmin.route) {
            DashboardScreenAdmin (
                onNavigate =  { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> navController.navigate(Screen.DashboardAdmin.route)
                        BottomNavDestination.USERS -> { navController.navigate(Screen.UsersAdmin.route) }
                        BottomNavDestination.MACHINES -> { navController.navigate(Screen.MachinesAdmin.route) }
                        else -> {/*Proximamente*/}
                    }
                }
            )
        }

        composable(Screen.MachinesAdmin.route) {
            AdminMachinesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> navController.navigate(Screen.DashboardAdmin.route)
                        BottomNavDestination.USERS -> { navController.navigate(Screen.UsersAdmin.route) }
                        BottomNavDestination.MACHINES -> { navController.navigate(Screen.MachinesAdmin.route) }
                        else -> {/*Proximamente*/}
                    }
                }
            )
        }

        composable(Screen.UsersAdmin.route) {
            AdminUsersScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> navController.navigate(Screen.DashboardAdmin.route)
                        BottomNavDestination.USERS -> { navController.navigate(Screen.UsersAdmin.route) }
                        BottomNavDestination.MACHINES -> { navController.navigate(Screen.MachinesAdmin.route) }
                        else -> {}
                    }
                }
            )
        }
    }
}
