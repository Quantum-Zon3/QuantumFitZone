package com.qz.quantumfitzone.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.ui.state.ExerciseProgressUiState
import com.qz.quantumfitzone.ui.state.ProgressHistoryPoint
import com.qz.quantumfitzone.ui.state.ProgressMetric
import com.qz.quantumfitzone.viewModel.EjercicioViewModel
import java.util.Locale

@Composable
fun ProgressScreen(
    exerciseId: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: EjercicioViewModel = viewModel()
) {
    val uiState = viewModel.progresoUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(exerciseId) {
        viewModel.loadProgreso(exerciseId)
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
                title = "Exercise Progress",
                onNavigateBack = onNavigateBack
            )

            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    DetailInfoCard(
                        title = "Loading progress",
                        message = "Estamos preparando la evolucion de este ejercicio."
                    )
                }

                uiState.notFound -> {
                    DetailInfoCard(
                        title = "Progress unavailable",
                        message = "No encontramos registros suficientes para esta vista."
                    )
                }

                else -> {
                    ProgressSummaryCard(uiState = uiState)
                    Spacer(Modifier.height(18.dp))

                    listOfNotNull(
                        uiState.weightMetric?.let { it to uiState.weightHistory },
                        uiState.repsMetric?.let { it to uiState.repsHistory },
                        uiState.setsMetric?.let { it to uiState.setsHistory }
                    ).forEach { (metric, history) ->
                        MetricOverviewCard(metric = metric, history = history)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ProgressSummaryCard(uiState: ExerciseProgressUiState) {
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
            text = "Current performance vs saved goals",
            color = DetailTextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MetricOverviewCard(
    metric: ProgressMetric,
    history: List<ProgressHistoryPoint>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DetailBgCard)
            .border(1.dp, DetailDividerColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = metric.label,
                    color = DetailTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${formatProgressValue(metric.current)} / ${formatProgressValue(metric.target)}",
                    color = DetailTextSecondary,
                    fontSize = 13.sp
                )
            }
            Text(
                text = "${metric.percent}%",
                color = DetailCyanPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(DetailBgCardAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(metric.percent.coerceIn(0, 100) / 100f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(DetailCyanPrimary.copy(alpha = 0.5f), DetailCyanPrimary)
                        )
                    )
            )
        }

        Spacer(Modifier.height(16.dp))
        ProgressChart(history = history)
    }
}

@Composable
private fun ProgressChart(history: List<ProgressHistoryPoint>) {
    if (history.isEmpty()) {
        Text(
            text = "No historical records yet.",
            color = DetailTextSecondary,
            fontSize = 12.sp
        )
        return
    }

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val horizontalPadding = 12.dp.toPx()
            val topPadding = 14.dp.toPx()
            val bottomPadding = 26.dp.toPx()
            val chartWidth = size.width - (horizontalPadding * 2)
            val chartHeight = size.height - topPadding - bottomPadding
            val stepX = if (history.size == 1) 0f else chartWidth / (history.size - 1)
            val maxValue = history.maxOf { it.value }.takeIf { it > 0f } ?: 1f

            repeat(3) { index ->
                val y = topPadding + (chartHeight / 2f) * index
                drawLine(
                    color = DetailDividerColor,
                    start = Offset(horizontalPadding, y),
                    end = Offset(size.width - horizontalPadding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val points = history.mapIndexed { index, point ->
                val x = horizontalPadding + (stepX * index)
                val y = topPadding + chartHeight - ((point.value / maxValue) * chartHeight)
                Offset(x, y)
            }

            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val previous = points[i - 1]
                    val current = points[i]
                    val controlX = (previous.x + current.x) / 2f
                    cubicTo(controlX, previous.y, controlX, current.y, current.x, current.y)
                }
            }

            drawPath(
                path = path,
                color = DetailCyanPrimary,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            points.forEach { point ->
                drawCircle(color = DetailBgDeep, radius = 7.dp.toPx(), center = point)
                drawCircle(color = DetailCyanPrimary, radius = 4.dp.toPx(), center = point)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            history.forEach { point ->
                Text(
                    text = point.label,
                    color = DetailTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.width(34.dp)
                )
            }
        }
    }
}

private fun formatProgressValue(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
private fun ProgressScreenPreview() {
    ProgressScreen(exerciseId = 1)
}
