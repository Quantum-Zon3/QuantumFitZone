package com.qz.quantumfitzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.data.model.RutinaEntity
import com.qz.quantumfitzone.viewModel.RoutineListViewModel

private val BgDeep = Color(0xFF080E1A)
private val BgCard = Color(0xFF0D1726)
private val CyanPrimary = Color(0xFF00D4FF)
private val TextPrimary = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DangerColor = Color(0xFFFF6B81)

private data class RoutineCardModel(
    val title: String,
    val category: String,
    val restLabel: String,
    val icon: ImageVector,
    val gradientColors: List<Color>
)

@Composable
fun MyRoutinesScreen(
    onNavigate: (BottomNavDestination) -> Unit = {},
    onEditRoutine: (Int?) -> Unit = {},
    onPlayRoutine: (Int) -> Unit = {},
    viewModel: RoutineListViewModel = viewModel()
) {
    val routines by viewModel.routines.collectAsStateWithLifecycle(initialValue = emptyList())
    var rutinaAEliminar by remember { mutableStateOf<RutinaEntity?>(null) }
    val playRoutineError = viewModel.playRoutineError

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = CyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "My Routines",
                    color = CyanPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.weight(1f))
                Box(Modifier.size(38.dp))
            }

            Spacer(Modifier.height(8.dp))

            if (routines.isEmpty()) {
                EmptyRoutineCard(onCreateRoutine = onEditRoutine)
            } else {
                routines.forEach { routine ->
                    RoutineCard(
                        routine = routine.toCardModel(),
                        onEditRoutine = { onEditRoutine(routine.id_rutina) },
                        onPlayRoutine = {
                            viewModel.startRoutineSession(routine.id_rutina) {
                                onPlayRoutine(routine.id_rutina)
                            }
                        },
                        onDeleteRoutine = { rutinaAEliminar = routine }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(100.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 120.dp)
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyanPrimary.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
                    .clickable { onEditRoutine(null) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva rutina",
                    tint = BgDeep,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        BottomNavBar(
            selected = BottomNavDestination.ROUTINES,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    rutinaAEliminar?.let { rutina ->
        AlertDialog(
            onDismissRequest = { rutinaAEliminar = null },
            containerColor = BgCard,
            title = {
                Text(
                    text = "Eliminar rutina",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Se eliminara \"${rutina.nombre}\" de tus rutinas guardadas.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarRutina(rutina)
                        rutinaAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { rutinaAEliminar = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    playRoutineError?.let { error ->
        AlertDialog(
            onDismissRequest = viewModel::dismissPlayRoutineError,
            containerColor = BgCard,
            title = {
                Text(
                    text = "No pudimos iniciar la rutina",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = error,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissPlayRoutineError) {
                    Text("Entendido", color = CyanPrimary)
                }
            }
        )
    }
}

private fun RutinaEntity.toCardModel(): RoutineCardModel {
    val palette = when (categoria.lowercase()) {
        "cardio" -> listOf(Color(0xFF1A1200), Color(0xFF2A1E00), Color(0xFF0E0A00))
        "flexibility" -> listOf(Color(0xFF001A0E), Color(0xFF002A14), Color(0xFF000E07))
        "hiit" -> listOf(Color(0xFF24000A), Color(0xFF3A0015), Color(0xFF140007))
        "recovery" -> listOf(Color(0xFF08192A), Color(0xFF0C2742), Color(0xFF06111D))
        else -> listOf(Color(0xFF0A1A2E), Color(0xFF0D2A3A), Color(0xFF061220))
    }

    return RoutineCardModel(
        title = nombre,
        category = categoria,
        restLabel = "Descanso ${descanso_segundos}s",
        icon = Icons.Default.FitnessCenter,
        gradientColors = palette
    )
}

@Composable
private fun EmptyRoutineCard(onCreateRoutine: (Int?) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(BgCard)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay rutinas guardadas",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Crea tu primera rutina y reutiliza ejercicios del catalogo en las siguientes.",
            color = TextSecondary,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(CyanPrimary)
                .clickable { onCreateRoutine(null) }
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Crear rutina",
                color = BgDeep,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RoutineCard(
    routine: RoutineCardModel,
    onEditRoutine: () -> Unit = {},
    onPlayRoutine: () -> Unit = {},
    onDeleteRoutine: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(colors = routine.gradientColors)
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = routine.title,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoutineBadge(
                    icon = Icons.Default.Schedule,
                    label = routine.restLabel
                )
                RoutineBadge(
                    icon = routine.icon,
                    label = routine.category
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RoutineActionButton(
                    label = "Play",
                    icon = Icons.Default.PlayArrow,
                    background = CyanPrimary,
                    contentColor = BgDeep,
                    onClick = onPlayRoutine
                )
                RoutineActionButton(
                    label = "Editar",
                    icon = Icons.Default.FitnessCenter,
                    background = Color(0x3300D4FF),
                    contentColor = TextPrimary,
                    onClick = onEditRoutine
                )
                RoutineActionButton(
                    label = "Eliminar",
                    icon = Icons.Default.DeleteOutline,
                    background = Color(0x33FF6B81),
                    contentColor = DangerColor,
                    onClick = onDeleteRoutine
                )
            }
        }
    }
}

@Composable
private fun RoutineActionButton(
    label: String,
    icon: ImageVector,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RoutineBadge(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x55000000))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun RoutinesScreenPreview() {
    MyRoutinesScreen()
}
