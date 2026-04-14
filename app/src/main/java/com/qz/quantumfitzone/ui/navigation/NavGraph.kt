package com.qz.quantumfitzone.ui.navigation
import androidx.collection.emptyLongSet
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quantumfitzone.QuantumFitzoneScreen
import com.qz.quantumfitzone.ui.screens.BottomNavDestination
import com.qz.quantumfitzone.ui.screens.EditRoutineScreen
import com.qz.quantumfitzone.ui.screens.LoginForm
import com.qz.quantumfitzone.ui.screens.MyRoutinesScreen
import com.qz.quantumfitzone.ui.screens.ProfileScreen
import com.qz.quantumfitzone.ui.screens.RegistroUsuarioForm
import com.qz.quantumfitzone.ui.screens.DashboardScreen
import com.qz.quantumfitzone.SupportScreen
import com.qz.quantumfitzone.ui.components.Screen
import com.qz.quantumfitzone.ui.screens.DashboardScreenAdmin

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
                onLogin        = { navController.navigate(Screen.Login.route) },
                onBack         = { navController.popBackStack() }
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
                onForgotPassword = { /* próximamente */ },
                onRegister = { navController.navigate(Screen.Registro.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignOut      = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES  -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.SUPPORT   -> navController.navigate(Screen.SupportCenter.route)
                        BottomNavDestination.PROFILE -> { /* ya estás aquí */ }
                        else -> { /* próximamente */ }
                    }
                }
            )
        }

        composable(Screen.MyRoutines.route) {
            MyRoutinesScreen(
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.PROFILE  -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        BottomNavDestination.SUPPORT   -> navController.navigate(Screen.SupportCenter.route)
                        BottomNavDestination.ROUTINES -> { /* ya estás aquí */ }
                        else -> { /* próximamente */ }
                    }
                },
                onEditRoutine = { navController.navigate(Screen.EditRoutine.route)}
            )
        }

        composable(Screen.EditRoutine.route) {
            EditRoutineScreen(
                onNavigateBack = { navController.navigate(Screen.MyRoutines.route) },
                onListo        = { navController.navigate(Screen.MyRoutines.route) {
                    popUpTo(Screen.MyRoutines.route) { inclusive = true }
                }},
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.PROFILE  -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.DASHBOARD -> { /* ya estás aquí */ }
                        BottomNavDestination.PROGRESS -> { /* próximamente */ }
                        BottomNavDestination.HISTORY -> { /* próximamente */ }
                        BottomNavDestination.SUPPORT   -> navController.navigate(Screen.SupportCenter.route)
                        else -> { /* luego agregaremos más */ }
                    }
                }
            )
        }

        composable(Screen.SupportCenter.route) {
            SupportScreen (
                onBack = { navController.navigate(Screen.Dashboard.route)},
                onNavigate = { destination ->
                    when (destination) {
                        BottomNavDestination.ROUTINES -> navController.navigate(Screen.MyRoutines.route)
                        BottomNavDestination.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomNavDestination.SUPPORT -> { /*Estas aqui*/}
                        BottomNavDestination.DASHBOARD -> navController.navigate(Screen.Dashboard.route)
                        else -> { /*Próximamente*/}
                    }
                },
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