package com.qz.quantumfitzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.viewModel.WorkoutHistoryViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val BgDeep = Color(0xFF080E1A)
private val BgCard = Color(0xFF0D1726)
private val BgCardAlt = Color(0xFF0F1C2E)
private val CyanPrimary = Color(0xFF00D4FF)
private val CyanGlow = Color(0x3300D4FF)
private val GoldElite = Color(0xFFFFD700)
private val TextPrimary = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DividerColor = Color(0xFF1A2F45)

private data class WorkoutSessionUi(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val durationMinutes: Int,
    val kcal: Int,
    val icon: ImageVector
)

private data class TopPerformanceUi(
    val title: String,
    val value: String,
    val unit: String,
    val date: String
)

@Composable
fun WorkoutHistoryScreen(
    onNavigate: (BottomNavDestination) -> Unit = {},
    onOpenRoutine: (Int) -> Unit = {},
    viewModel: WorkoutHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sessions = remember(uiState.sesiones) {
        uiState.sesiones.mapNotNull { it.toUiModelOrNull() }.sortedByDescending { it.date }
    }

    val latestSessionDate = sessions.firstOrNull()?.date
    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var selectedDay by rememberSaveable { mutableIntStateOf(-1) }
    var monthInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(latestSessionDate) {
        if (!monthInitialized && latestSessionDate != null) {
            currentMonth = YearMonth.from(latestSessionDate)
            selectedDay = latestSessionDate.dayOfMonth
            monthInitialized = true
        }
    }

    val activeDays = sessions
        .filter {
            it.date.year == currentMonth.year &&
                it.date.monthValue == currentMonth.monthValue
        }
        .map { it.date.dayOfMonth }
        .toSet()

    LaunchedEffect(currentMonth, activeDays) {
        when {
            activeDays.isNotEmpty() && selectedDay !in activeDays -> {
                selectedDay = activeDays.max()
            }

            activeDays.isEmpty() && selectedDay != -1 -> {
                selectedDay = -1
            }
        }
    }

    val sessionsForSelectedDay = sessions.filter {
        it.date.year == currentMonth.year &&
            it.date.monthValue == currentMonth.monthValue &&
            it.date.dayOfMonth == selectedDay
    }

    val groupedSessions = sessions.groupBy { it.date }
    val topPerformance = remember(sessions) {
        sessions.maxByOrNull { it.kcal }?.let {
            TopPerformanceUi(
                title = it.title,
                value = it.kcal.toString(),
                unit = "kcal",
                date = formatShortDate(it.date)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Workout History",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanGlow)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            if (topPerformance != null) {
                TopPerformanceCard(performance = topPerformance)
                Spacer(Modifier.height(16.dp))
            }

            CalendarCard(
                yearMonth = currentMonth,
                activeDays = activeDays,
                selectedDay = selectedDay,
                onPrevMonth = {
                    currentMonth = currentMonth.minusMonths(1)
                    selectedDay = -1
                },
                onNextMonth = {
                    currentMonth = currentMonth.plusMonths(1)
                    selectedDay = -1
                },
                onSelectDay = { selectedDay = it }
            )

            Spacer(Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    InfoCard(
                        title = "Loading history",
                        message = "Estamos preparando tus sesiones registradas."
                    )
                }

                uiState.correoUsuario.isBlank() -> {
                    InfoCard(
                        title = "No active session",
                        message = "Inicia sesion para ver tu historial de entrenamiento."
                    )
                }

                sessions.isEmpty() -> {
                    InfoCard(
                        title = "No workouts yet",
                        message = "Todavia no hay sesiones guardadas para este perfil."
                    )
                }

                else -> {
                    SelectedDayCard(
                        currentMonth = currentMonth,
                        selectedDay = selectedDay,
                        sessions = sessionsForSelectedDay
                    )

                    Spacer(Modifier.height(20.dp))

                    groupedSessions.forEach { (date, dailySessions) ->
                        Text(
                            text = formatDate(date),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        dailySessions.forEach { session ->
                            SessionRow(
                                session = session,
                                onOpenRoutine = { onOpenRoutine(session.id) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }

        BottomNavBar(
            selected = BottomNavDestination.HISTORY,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopPerformanceCard(performance: TopPerformanceUi) {
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
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(CyanGlow, CyanPrimary.copy(alpha = 0.3f), CyanGlow)
                ),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldElite,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "TOP PERFORMANCE",
                    color = GoldElite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
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
                        text = performance.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Highest burn - ${performance.date}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${performance.value} ${performance.unit}",
                    color = CyanPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun CalendarCard(
    yearMonth: YearMonth,
    activeDays: Set<Int>,
    selectedDay: Int,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (Int) -> Unit
) {
    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val firstDow = yearMonth.atDay(1).dayOfWeek.value % 7
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevMonth, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = TextSecondary
                )
            }
            Text(
                text = "$monthName ${yearMonth.year}",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayHeaders.forEach { dayHeader ->
                Text(
                    text = dayHeader,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val totalCells = firstDow + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDow + 1

                    if (day < 1 || day > daysInMonth) {
                        Box(modifier = Modifier.size(32.dp))
                    } else {
                        CalendarDay(
                            day = day,
                            isSelected = day == selectedDay,
                            hasActivity = day in activeDays,
                            onClick = { onSelectDay(day) }
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
        modifier = Modifier.size(32.dp),
        contentAlignment = Alignment.Center
    ) {
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
                text = day.toString(),
                color = when {
                    isSelected -> BgDeep
                    hasActivity -> TextPrimary
                    else -> TextSecondary
                },
                fontSize = 13.sp,
                fontWeight = if (isSelected || hasActivity) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
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

@Composable
private fun SelectedDayCard(
    currentMonth: YearMonth,
    selectedDay: Int,
    sessions: List<WorkoutSessionUi>
) {
    val title = if (selectedDay > 0) {
        val date = currentMonth.atDay(selectedDay)
        "Selected day - ${formatDate(date)}"
    } else {
        "Selected day"
    }

    val message = when {
        selectedDay <= 0 -> "Select a highlighted day in the calendar."
        sessions.isEmpty() -> "No workouts were recorded for this day."
        sessions.size == 1 -> "1 session recorded."
        else -> "${sessions.size} sessions recorded."
    }

    InfoCard(title = title, message = message)
}

@Composable
private fun InfoCard(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun SessionRow(
    session: WorkoutSessionUi,
    onOpenRoutine: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenRoutine)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyanGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = session.icon,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "${formatDuration(session.durationMinutes)} - ${session.kcal} kcal",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(BgCardAlt)
                .border(1.dp, DividerColor, RoundedCornerShape(20.dp))
                .clickable(onClick = onOpenRoutine)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Summary",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun HistorialEntrenamientoEntity.toUiModelOrNull(): WorkoutSessionUi? {
    val parsedDate = runCatching { LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
        ?: return null

    return WorkoutSessionUi(
        id = id_historial,
        date = parsedDate,
        title = titulo,
        durationMinutes = duracion_minutos,
        kcal = kcal,
        icon = categoriaToIcon(categoria)
    )
}

private fun categoriaToIcon(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "cardio" -> Icons.Default.DirectionsRun
        "mobility" -> Icons.Default.SelfImprovement
        "hiit" -> Icons.Default.Bolt
        "calories" -> Icons.Default.LocalFireDepartment
        else -> Icons.Default.FitnessCenter
    }
}

private fun formatDate(date: LocalDate): String {
    val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    return "$month ${date.dayOfMonth}, ${date.year}"
}

private fun formatShortDate(date: LocalDate): String {
    val month = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    return "$month ${date.dayOfMonth}"
}

private fun formatDuration(durationMinutes: Int): String {
    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    return if (hours > 0) {
        "$hours hr ${minutes} mins"
    } else {
        "$minutes mins"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun WorkoutHistoryScreenPreview() {
    WorkoutHistoryScreen()
}
