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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.viewModel.ExerciseHistoryDetailUiState
import com.qz.quantumfitzone.viewModel.ExerciseHistoryDetailViewModel

@Composable
fun ExerciseHistoryDetailScreen(
    exerciseId: Int,
    onNavigateBack: () -> Unit = {},
    onOpenProgress: (Int) -> Unit = {},
    viewModel: ExerciseHistoryDetailViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(exerciseId) {
        viewModel.load(exerciseId)
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
                title = "Exercise Detail",
                onNavigateBack = onNavigateBack
            )

            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    DetailInfoCard(
                        title = "Loading exercise",
                        message = "Estamos preparando las estadisticas registradas."
                    )
                }

                uiState.notFound -> {
                    DetailInfoCard(
                        title = "Exercise unavailable",
                        message = "No encontramos ese ejercicio dentro del historial."
                    )
                }

                else -> {
                    ExerciseHeaderCard(uiState = uiState)
                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Recorded metrics",
                        color = DetailTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    MetricProgressCard(
                        label = "Weight",
                        current = uiState.weight?.let { "${formatNumber(it)} kg" } ?: "No data",
                        target = uiState.targetWeight?.let { "${formatNumber(it)} kg" } ?: "No target",
                        progress = uiState.weightProgress
                    )
                    Spacer(Modifier.height(10.dp))
                    MetricProgressCard(
                        label = "Repetitions",
                        current = uiState.reps?.let { "$it reps" } ?: "No data",
                        target = uiState.targetReps?.let { "$it reps" } ?: "No target",
                        progress = uiState.repsProgress
                    )
                    Spacer(Modifier.height(10.dp))
                    MetricProgressCard(
                        label = "Sets",
                        current = uiState.sets?.let { "$it sets" } ?: "No data",
                        target = uiState.targetSets?.let { "$it sets" } ?: "No target",
                        progress = uiState.setsProgress
                    )

                    Spacer(Modifier.height(18.dp))

                    ProgressActionCard(
                        onClick = { onOpenProgress(uiState.exerciseId) }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ExerciseHeaderCard(uiState: ExerciseHistoryDetailUiState) {
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
            text = uiState.machineName,
            color = DetailTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (uiState.date.isBlank()) "No date recorded" else formatIsoDate(uiState.date),
            color = DetailTextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MetricProgressCard(
    label: String,
    current: String,
    target: String,
    progress: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DetailBgCard)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = DetailTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = progress?.let { "$it%" } ?: "--",
                color = DetailCyanPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Current: $current",
            color = DetailTextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = "Goal: $target",
            color = DetailTextSecondary,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(DetailBgCardAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((progress ?: 0).coerceIn(0, 100) / 100f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(DetailCyanPrimary.copy(alpha = 0.6f), DetailCyanPrimary)
                        )
                    )
            )
        }
    }
}

@Composable
private fun ProgressActionCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DetailBgCard)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Open progress",
                color = DetailTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Compara el valor actual contra la meta y revisa su evolucion.",
                color = DetailTextSecondary,
                fontSize = 13.sp
            )
        }
        Text(
            text = "View",
            color = DetailCyanPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
private fun ExerciseHistoryDetailPreview() {
    ExerciseHistoryDetailScreen(exerciseId = 1)
}
