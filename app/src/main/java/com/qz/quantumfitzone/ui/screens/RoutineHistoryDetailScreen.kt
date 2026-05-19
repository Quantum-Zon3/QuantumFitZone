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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.qz.quantumfitzone.ui.state.RoutineHistoryDetailUiState
import com.qz.quantumfitzone.ui.state.RoutineHistoryExerciseItem
import com.qz.quantumfitzone.viewModel.HistorialEntrenamientoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

internal val DetailBgDeep = Color(0xFF080E1A)
internal val DetailBgCard = Color(0xFF0D1726)
internal val DetailBgCardAlt = Color(0xFF0F1C2E)
internal val DetailCyanPrimary = Color(0xFF00D4FF)
internal val DetailCyanGlow = Color(0x3300D4FF)
internal val DetailTextPrimary = Color(0xFFE8F4FF)
internal val DetailTextSecondary = Color(0xFF6B8FAB)
internal val DetailDividerColor = Color(0xFF1A2F45)

@Composable
fun RoutineHistoryDetailScreen(
    historyId: Int,
    onNavigateBack: () -> Unit = {},
    onOpenExercise: (Int) -> Unit = {},
    viewModel: HistorialEntrenamientoViewModel = viewModel()
) {
    val uiState = viewModel.rutinaDetalleUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(historyId) {
        viewModel.loadRutinaDetalle(historyId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBgDeep)
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            DetailTopBar(
                title = "Routine Detail",
                onNavigateBack = onNavigateBack
            )

            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    DetailInfoCard(
                        title = "Loading routine",
                        message = "Estamos preparando los ejercicios de esta sesion."
                    )
                }

                uiState.notFound -> {
                    DetailInfoCard(
                        title = "Session unavailable",
                        message = "No encontramos esta rutina en el historial."
                    )
                }

                else -> {
                    RoutineSummaryCard(uiState = uiState)
                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Exercises completed",
                        color = DetailTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    if (uiState.exercises.isEmpty()) {
                        DetailInfoCard(
                            title = "No exercises found",
                            message = "Esta rutina no tiene ejercicios registrados para ese dia."
                        )
                    } else {
                        uiState.exercises.forEach { exercise ->
                            ExerciseHistoryRow(
                                exercise = exercise,
                                onClick = { onOpenExercise(exercise.historyExerciseId) }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
internal fun DetailTopBar(
    title: String,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DetailTextPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            color = DetailCyanPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun RoutineSummaryCard(uiState: RoutineHistoryDetailUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DetailBgCard)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = uiState.title,
            color = DetailTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = formatIsoDate(uiState.date),
            color = DetailTextSecondary,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryChip(
                label = formatDurationLabel(uiState.durationMinutes),
                icon = Icons.Default.FitnessCenter
            )
            SummaryChip(
                label = "${uiState.kcal} kcal",
                icon = Icons.Default.Bolt
            )
        }
    }
}

@Composable
private fun SummaryChip(label: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DetailBgCardAlt)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DetailCyanPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = DetailTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ExerciseHistoryRow(
    exercise: RoutineHistoryExerciseItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DetailBgCard)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DetailCyanGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = DetailCyanPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.machineName,
                color = DetailTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = buildMetricsLine(exercise),
                color = DetailTextSecondary,
                fontSize = 12.sp
            )
        }

        Text(
            text = "Details",
            color = DetailCyanPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun buildMetricsLine(exercise: RoutineHistoryExerciseItem): String {
    val chunks = buildList {
        exercise.weight?.let { add("${formatNumber(it)} kg") }
        exercise.reps?.let { add("$it reps") }
        exercise.sets?.let { add("$it sets") }
    }
    return if (chunks.isEmpty()) "No metrics recorded" else chunks.joinToString(" - ")
}

internal fun formatIsoDate(date: String): String {
    val parsed = runCatching { LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
        ?: return date
    val month = parsed.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    return "$month ${parsed.dayOfMonth}, ${parsed.year}"
}

internal fun formatNumber(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.1f", value)
}

@Composable
internal fun DetailInfoCard(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DetailBgCard)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Text(
            text = title,
            color = DetailTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            color = DetailTextSecondary,
            fontSize = 13.sp
        )
    }
}

internal fun formatDurationLabel(durationMinutes: Int): String {
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
private fun RoutineHistoryDetailPreview() {
    RoutineHistoryDetailScreen(historyId = 1)
}
