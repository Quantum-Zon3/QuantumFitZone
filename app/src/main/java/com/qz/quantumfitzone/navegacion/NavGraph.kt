package com.qz.quantumfitzone.navegacion
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.quantumfitzone.QuantumFitzoneScreen
import com.qz.quantumfitzone.BottomNavDestination
import com.qz.quantumfitzone.EditRoutineScreen
import com.qz.quantumfitzone.LoginForm
import com.qz.quantumfitzone.MyRoutinesScreen
import com.qz.quantumfitzone.ProfileScreen
import com.qz.quantumfitzone.RegistroUsuarioForm
import com.qz.quantumfitzone.DashboardScreen

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
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Login.route) {
            LoginForm(
                onBack = { navController.popBackStack() },
                onLogin = { navController.navigate(Screen.Dashboard.route) },
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
                        BottomNavDestination.SUPPORT -> { /* próximamente */ }
                        else -> { /* luego agregaremos más */ }
                    }
                }
            )
        }
    }
}