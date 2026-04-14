package com.qz.quantumfitzone.ui.navigation
import androidx.collection.emptyLongSet

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quantumfitzone.QuantumFitzoneScreen
import com.qz.quantumfitzone.SupportScreen
import com.qz.quantumfitzone.ui.components.Screen
import com.qz.quantumfitzone.ui.screens.BottomNavDestination
import com.qz.quantumfitzone.ui.screens.DashboardScreen
import com.qz.quantumfitzone.ui.screens.EditRoutineScreen
import com.qz.quantumfitzone.ui.screens.LoginForm
import com.qz.quantumfitzone.ui.screens.MyRoutinesScreen
import com.qz.quantumfitzone.ui.screens.ProfileScreen
import com.qz.quantumfitzone.ui.screens.ProgressScreen
import com.qz.quantumfitzone.ui.screens.RegistroUsuarioForm
import com.qz.quantumfitzone.ui.screens.DashboardScreen
import com.qz.quantumfitzone.SupportScreen
import com.qz.quantumfitzone.ui.screens.DashboardScreenAdmin
import com.qz.quantumfitzone.ui.screens.WorkoutHistoryScreen

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
                onDashboard = { navController.navigate(Screen.Dashboard.route) }
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
                onLogin = { navController.navigate(Screen.Profile.route) },
                onForgotPassword = { },
                onRegister = { navController.navigate(Screen.Registro.route) }
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
                        BottomNavDestination.PROGRESS -> navController.navigate(Screen.Progress.route)
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
                        BottomNavDestination.PROGRESS -> navController.navigate(Screen.Progress.route)
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
                        BottomNavDestination.PROGRESS -> navController.navigate(Screen.Progress.route)
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
                        BottomNavDestination.PROGRESS -> navController.navigate(Screen.Progress.route)
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
                        BottomNavDestination.PROGRESS -> navController.navigate(Screen.Progress.route)
                        BottomNavDestination.HISTORY -> {navController.navigate(Screen.WorkoutHistory.route) }
                        else -> {}
                    }
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.SUPPORT -> navController.navigate(Screen.SupportCenter.route)
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.HISTORY -> navController.navigate(Screen.WorkoutHistory.route)
                        BottomNavDestination.PROGRESS -> {navController.navigate(Screen.Progress.route) }
                        else -> {}
                    }
                }
            )
        }

        composable(Screen.DashboardAdmin.route) {
            DashboardScreenAdmin (
                onNavigate =  { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> navController.navigate(Screen.DashboardAdmin.route)
                        BottomNavDestination.USERS -> {/*Proximamente*/}
                        BottomNavDestination.MACHINES -> {/*Proximamente*/}
                        else -> {/*Proximamente*/}
                    }
                },
            )
        }
    }
}
