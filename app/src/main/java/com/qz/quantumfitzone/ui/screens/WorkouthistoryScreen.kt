package com.qz.quantumfitzone.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// ── Colores (mismos que ProfileScreen) ───────────────────────────────────────
private val BgDeep        = Color(0xFF080E1A)
private val BgCard        = Color(0xFF0D1726)
private val BgCardAlt     = Color(0xFF0F1C2E)
private val CyanPrimary   = Color(0xFF00D4FF)
private val CyanDim       = Color(0xFF0A8FAA)
private val CyanGlow      = Color(0x3300D4FF)
private val GoldElite     = Color(0xFFFFD700)
private val TextPrimary   = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DividerColor  = Color(0xFF1A2F45)

// ── Modelos ───────────────────────────────────────────────────────────────────
private data class WorkoutSession(
    val date: LocalDate,
    val title: String,
    val duration: String,
    val kcal: Int,
    val icon: ImageVector
)

private data class TopPerformance(
    val exercise: String,
    val value: String,
    val unit: String,
    val date: String,
    val isPersonalRecord: Boolean = true
)

// ── Datos de muestra ──────────────────────────────────────────────────────────
private val sampleSessions = listOf(
    WorkoutSession(
        date     = LocalDate.of(2023, 8, 24),
        title    = "Full Body Power",
        duration = "1 hr 15 mins",
        kcal     = 480,
        icon     = Icons.Default.FitnessCenter
    ),
    WorkoutSession(
        date     = LocalDate.of(2023, 8, 22),
        title    = "Neon Cardio Sprint",
        duration = "45 mins",
        kcal     = 320,
        icon     = Icons.Default.DirectionsRun
    ),
    WorkoutSession(
        date     = LocalDate.of(2023, 8, 20),
        title    = "Cyber Core Flow",
        duration = "30 mins",
        kcal     = 150,
        icon     = Icons.Default.SelfImprovement
    ),
    WorkoutSession(
        date     = LocalDate.of(2023, 8, 17),
        title    = "Quantum Deadlift",
        duration = "50 mins",
        kcal     = 410,
        icon     = Icons.Default.FitnessCenter
    ),
    WorkoutSession(
        date     = LocalDate.of(2023, 8, 15),
        title    = "Neural HIIT Blast",
        duration = "35 mins",
        kcal     = 370,
        icon     = Icons.Default.Bolt
    )
)

private val topPerformance = TopPerformance(
    exercise          = "Quantum Deadlift",
    value             = "185",
    unit              = "kg",
    date              = "Aug 22",
    isPersonalRecord  = true
)

// ── Pantalla principal ────────────────────────────────────────────────────────
@Composable
fun WorkoutHistoryScreen(
    onNavigate: (BottomNavDestination) -> Unit = {}
) {
    // Estado del calendario
    var currentMonth by remember { mutableStateOf(YearMonth.of(2023, 8)) }
    var selectedDay  by remember { mutableStateOf(24) }

    // Días con entrenamiento en el mes
    val activeDays = sampleSessions
        .filter {
            it.date.year == currentMonth.year &&
                    it.date.monthValue == currentMonth.monthValue
        }
        .map { it.date.dayOfMonth }
        .toSet()

    // Sesiones del día seleccionado
    val sessionsForSelected = sampleSessions.filter {
        it.date.year        == currentMonth.year &&
                it.date.monthValue  == currentMonth.monthValue &&
                it.date.dayOfMonth  == selectedDay
    }

    // Todas las sesiones agrupadas por fecha (para la lista completa)
    val groupedSessions = sampleSessions
        .sortedByDescending { it.date }
        .groupBy { it.date }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        // Fondo degradado superior (igual que ProfileScreen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A1A30), Color.Transparent)
                    )
                )
        )

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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text          = "Workout History",
                    color         = TextPrimary,
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.weight(1f))
                // Botón filtro
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanGlow)
                        .clickable { /* filtros */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint               = CyanPrimary,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Top Performance Card ───────────────────────────────────────────
            TopPerformanceCard(performance = topPerformance)

            Spacer(Modifier.height(16.dp))

            // ── Calendario ────────────────────────────────────────────────────
            CalendarCard(
                yearMonth    = currentMonth,
                activeDays   = activeDays,
                selectedDay  = selectedDay,
                onPrevMonth  = {
                    currentMonth = currentMonth.minusMonths(1)
                    selectedDay  = -1
                },
                onNextMonth  = {
                    currentMonth = currentMonth.plusMonths(1)
                    selectedDay  = -1
                },
                onSelectDay  = { selectedDay = it }
            )

            Spacer(Modifier.height(20.dp))

            // ── Lista de sesiones agrupadas por fecha ─────────────────────────
            groupedSessions.forEach { (date, sessions) ->
                // Cabecera de fecha
                Text(
                    text     = formatDate(date),
                    color    = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight  = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                sessions.forEach { session ->
                    SessionRow(session = session)
                    Spacer(Modifier.height(8.dp))
                }
            }

            // Espacio para la bottom nav
            Spacer(Modifier.height(80.dp))
        }

        // ── Bottom Nav ────────────────────────────────────────────────────────
        BottomNavBar(
            selected    = BottomNavDestination.HISTORY,
            onItemClick = onNavigate,
            modifier    = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Top Performance Card ──────────────────────────────────────────────────────
@Composable
private fun TopPerformanceCard(performance: TopPerformance) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0A1F35), Color(0xFF0D2A3A))
                )
            )
            .border(
                width  = 1.dp,
                brush  = Brush.horizontalGradient(
                    listOf(CyanGlow, CyanPrimary.copy(alpha = 0.3f), CyanGlow)
                ),
                shape  = RoundedCornerShape(18.dp)
            )
    ) {
        // Línea de acento superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, CyanPrimary.copy(0.7f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Etiqueta TOP PERFORMANCE
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint               = GoldElite,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text          = "TOP PERFORMANCE",
                    color         = GoldElite,
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = performance.exercise,
                        color      = TextPrimary,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = if (performance.isPersonalRecord)
                            "New Personal Record · ${performance.date}"
                        else
                            performance.date,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Valor destacado
                Text(
                    text       = "${performance.value} ${performance.unit}",
                    color      = CyanPrimary,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ── Calendario ────────────────────────────────────────────────────────────────
@Composable
private fun CalendarCard(
    yearMonth   : YearMonth,
    activeDays  : Set<Int>,
    selectedDay : Int,
    onPrevMonth : () -> Unit,
    onNextMonth : () -> Unit,
    onSelectDay : (Int) -> Unit
) {
    val monthName  = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year       = yearMonth.year
    val firstDow   = yearMonth.atDay(1).dayOfWeek.value % 7  // 0 = Sun
    val daysInMonth = yearMonth.lengthOfMonth()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Cabecera mes/año + flechas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevMonth, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Mes anterior",
                    tint = TextSecondary
                )
            }
            Text(
                text       = "$monthName $year",
                color      = TextPrimary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Mes siguiente",
                    tint = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Días de la semana
        val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayHeaders.forEach { d ->
                Text(
                    text      = d,
                    color     = TextSecondary,
                    fontSize  = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier  = Modifier.width(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Cuadrícula de días
        val totalCells = firstDow + daysInMonth
        val rows       = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDow + 1

                    if (day < 1 || day > daysInMonth) {
                        Box(modifier = Modifier.width(32.dp).height(32.dp))
                    } else {
                        CalendarDay(
                            day        = day,
                            isSelected = day == selectedDay,
                            hasActivity = day in activeDays,
                            onClick    = { onSelectDay(day) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    hasActivity: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Fondo del día seleccionado
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Text(
                text       = day.toString(),
                color      = when {
                    isSelected  -> BgDeep
                    hasActivity -> TextPrimary
                    else        -> TextSecondary
                },
                fontSize   = 13.sp,
                fontWeight = if (isSelected || hasActivity) FontWeight.Bold else FontWeight.Normal,
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center
            )
            // Punto indicador de actividad
            if (hasActivity && !isSelected) {
                Spacer(Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary)
                )
            }
        }
    }
}

// ── Session Row ───────────────────────────────────────────────────────────────
@Composable
private fun SessionRow(session: WorkoutSession) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
            .clickable { /* abrir detalle */ }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono del ejercicio
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyanGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = session.icon,
                contentDescription = null,
                tint               = CyanPrimary,
                modifier           = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        // Nombre + duración · kcal
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = session.title,
                color      = TextPrimary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text     = "${session.duration} · ${session.kcal} kcal",
                color    = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Botón Summary
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(BgCardAlt)
                .border(1.dp, DividerColor, RoundedCornerShape(20.dp))
                .clickable { /* ver resumen */ }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "Summary",
                color      = TextPrimary,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Helper ────────────────────────────────────────────────────────────────────
private fun formatDate(date: LocalDate): String {
    val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    return "$month ${date.dayOfMonth}, ${date.year}"
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun WorkoutHistoryScreenPreview() {
    WorkoutHistoryScreen()
}