package com.qz.quantumfitzone

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.qz.quantumfitzone.navegacion.Screen

// ── Colores ───────────────────────────────────────────────────────────────────
private val BgDeep        = Color(0xFF080E1A)
private val BgCard        = Color(0xFF0D1726)
private val CyanPrimary   = Color(0xFF00D4FF)
private val CyanGlow      = Color(0x3300D4FF)
private val TextPrimary   = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)

// ── Modelo de datos ───────────────────────────────────────────────────────────
private data class Routine(
    val title: String,
    val duration: String,
    val level: String,
    val levelIcon: ImageVector,
    val gradientColors: List<Color>
)

private val sampleRoutines = listOf(
    Routine(
        title          = "Neon Strength",
        duration       = "45 mins",
        level          = "Intermediate",
        levelIcon      = Icons.Default.FitnessCenter,
        gradientColors = listOf(Color(0xFF0A1A2E), Color(0xFF0D2A3A), Color(0xFF061220))
    ),
    Routine(
        title          = "Quantum Cardio",
        duration       = "30 mins",
        level          = "Advanced",
        levelIcon      = Icons.Default.DirectionsRun,
        gradientColors = listOf(Color(0xFF1A1200), Color(0xFF2A1E00), Color(0xFF0E0A00))
    ),
    Routine(
        title          = "Cyber Yoga",
        duration       = "60 mins",
        level          = "Beginner",
        levelIcon      = Icons.Default.SelfImprovement,
        gradientColors = listOf(Color(0xFF001A0E), Color(0xFF002A14), Color(0xFF000E07))
    )
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun MyRoutinesScreen(
    onNavigate: (BottomNavDestination) -> Unit = {},
    onEditRoutine: () -> Unit = {}
)
{
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
            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .border(1.dp, CyanPrimary, CircleShape),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .border(1.5.dp, CyanPrimary, CircleShape)
                        .clip(CircleShape)
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint               = CyanPrimary,
                        modifier           = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text       = "My Routines",
                    color      = CyanPrimary,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.weight(1f))

                // Espacio simetría
                Box(Modifier.size(38.dp))
            }

            Spacer(Modifier.height(8.dp))

            // ── Lista de rutinas ──────────────────────────────────────────────
            sampleRoutines.forEach { routine ->
                RoutineCard(
                    routine       = routine,
                    onEditRoutine = onEditRoutine  // ← agregar esto
                )
                Spacer(Modifier.height(16.dp))
            }


            // Espacio para el FAB y bottom nav
            Spacer(Modifier.height(100.dp))
        }

        // ── FAB ───────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 120.dp)
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow
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
            // Botón
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
                    .clickable { /* próximamente */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Nueva rutina",
                    tint               = BgDeep,
                    modifier           = Modifier.size(26.dp)
                )
            }
        }

        // ── Bottom Nav ────────────────────────────────────────────────────────
        BottomNavBar(
            selected    = BottomNavDestination.ROUTINES,
            onItemClick = onNavigate,
            modifier    = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Routine Card ──────────────────────────────────────────────────────────────
@Composable
private fun RoutineCard(
    routine: Routine,
    onEditRoutine: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp)
            .clickable { onEditRoutine() }
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(colors = routine.gradientColors)
            )
    ) {
        // Línea de acento cyan en el borde superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, CyanPrimary.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        // Contenido
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text       = routine.title,
                color      = TextPrimary,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge duración
                RoutineBadge(
                    icon  = Icons.Default.Schedule,
                    label = routine.duration
                )
                // Badge nivel
                RoutineBadge(
                    icon  = routine.levelIcon,
                    label = routine.level
                )
            }
        }

        // Botón play
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(CyanGlow)
                .clickable { /* decorativo */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint               = CyanPrimary,
                modifier           = Modifier.size(22.dp)
            )
        }
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
            imageVector        = icon,
            contentDescription = null,
            tint               = TextSecondary,
            modifier           = Modifier.size(12.dp)
        )
        Text(
            text     = label,
            color    = TextPrimary,
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

