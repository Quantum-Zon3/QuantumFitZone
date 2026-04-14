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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProgressBgDeep = Color(0xFF080E1A)
private val ProgressBgCard = Color(0xFF0D1726)
private val ProgressBgCardAlt = Color(0xFF102032)
private val ProgressCyan = Color(0xFF00D4FF)
private val ProgressCyanSoft = Color(0x3300D4FF)
private val ProgressPink = Color(0xFFFF33B8)
private val ProgressPinkSoft = Color(0x33FF33B8)
private val ProgressTextPrimary = Color(0xFFE8F4FF)
private val ProgressTextSecondary = Color(0xFF6B8FAB)
private val ProgressDivider = Color(0xFF17334B)

private data class ChartPoint(val xLabel: String, val value: Float)

private data class Achievement(
    val title: String,
    val subtitle: String,
    val accent: Color,
    val glow: Color
)

private val weightSeries = listOf(
    ChartPoint("W1", 0.70f),
    ChartPoint("W2", 0.66f),
    ChartPoint("W3", 0.60f),
    ChartPoint("W4", 0.48f)
)

private val muscleSeries = listOf(
    ChartPoint("W1", 0.54f),
    ChartPoint("W2", 0.58f),
    ChartPoint("W3", 0.67f),
    ChartPoint("W4", 0.78f)
)

private val trainingVolume = listOf(
    0.38f, 0.44f, 0.52f, 0.62f, 0.78f, 0.56f, 0.48f
)

private val achievements = listOf(
    Achievement(
        title = "7 Day Streak",
        subtitle = "Unstoppable",
        accent = ProgressPink,
        glow = ProgressPinkSoft
    ),
    Achievement(
        title = "10k Club",
        subtitle = "Volume Goal",
        accent = ProgressCyan,
        glow = ProgressCyanSoft
    )
)

@Composable
fun ProgressScreen(
    onNavigateBack: () -> Unit = {},
    onNavigate: (BottomNavDestination) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProgressBgDeep)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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
            ProgressTopBar(onNavigateBack = onNavigateBack)

            Spacer(modifier = Modifier.height(18.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                BodyCompositionCard()

                Spacer(modifier = Modifier.height(18.dp))

                TrainingVolumeCard()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Quantum Achievements",
                    color = ProgressTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    achievements.forEach { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(88.dp))
        }

        BottomNavBar(
            selected = BottomNavDestination.PROGRESS,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ProgressTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ProgressTextPrimary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Quantum Progress",
            color = ProgressCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun BodyCompositionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ProgressBgCard)
            .border(1.dp, ProgressDivider, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BODY COMPOSITION",
                color = ProgressTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            PillBadge(text = "-2.5%")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "152 lbs",
                color = ProgressCyan,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = " / 34%",
                color = ProgressPink,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendItem(label = "Weight", color = ProgressCyan)
            LegendItem(label = "Muscle Mass", color = ProgressPink)
        }

        Spacer(modifier = Modifier.height(16.dp))

        BodyCompositionChart(
            weightPoints = weightSeries,
            musclePoints = muscleSeries
        )
    }
}

@Composable
private fun TrainingVolumeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ProgressBgCard)
            .border(1.dp, ProgressDivider, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Training Volume",
            color = ProgressTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        TrainingBars(values = trainingVolume)
    }
}

@Composable
private fun TrainingBars(values: List<Float>) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val barWidth = size.width / (values.size * 1.8f)
            val gap = (size.width - (barWidth * values.size)) / (values.size - 1).coerceAtLeast(1)

            for (i in values.indices) {
                val barHeight = size.height * values[i]
                val x = i * (barWidth + gap)
                val top = size.height - barHeight
                val isActive = i == 4
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = if (isActive) {
                            listOf(ProgressCyan, ProgressCyan.copy(alpha = 0.30f))
                        } else {
                            listOf(
                                Color(0xFF1B3147),
                                Color(0xFF102032)
                            )
                        }
                    ),
                    topLeft = Offset(x, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(18f, 18f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    color = if (index == 4) ProgressCyan else ProgressTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (index == 4) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.width(24.dp)
                )
            }
        }
    }
}

@Composable
private fun BodyCompositionChart(
    weightPoints: List<ChartPoint>,
    musclePoints: List<ChartPoint>
) {
    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val leftPadding = 10.dp.toPx()
            val rightPadding = 10.dp.toPx()
            val topPadding = 14.dp.toPx()
            val bottomPadding = 22.dp.toPx()
            val chartWidth = size.width - leftPadding - rightPadding
            val chartHeight = size.height - topPadding - bottomPadding
            val stepX = chartWidth / (weightPoints.size - 1).coerceAtLeast(1)

            for (i in 0..2) {
                val y = topPadding + (chartHeight / 2f) * i
                drawLine(
                    color = ProgressDivider,
                    start = Offset(leftPadding, y),
                    end = Offset(size.width - rightPadding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            fun pointOffset(index: Int, value: Float): Offset {
                val x = leftPadding + (stepX * index)
                val y = topPadding + (chartHeight * (1f - value))
                return Offset(x, y)
            }

            fun smoothPath(points: List<Offset>): Path {
                val path = Path()
                if (points.isEmpty()) return path
                path.moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val previous = points[i - 1]
                    val current = points[i]
                    val controlX = (previous.x + current.x) / 2f
                    path.cubicTo(
                        controlX,
                        previous.y,
                        controlX,
                        current.y,
                        current.x,
                        current.y
                    )
                }
                return path
            }

            val cyanOffsets = weightPoints.mapIndexed { index, point -> pointOffset(index, point.value) }
            val pinkOffsets = musclePoints.mapIndexed { index, point -> pointOffset(index, point.value) }

            drawPath(
                path = smoothPath(cyanOffsets),
                color = ProgressCyan,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = smoothPath(pinkOffsets),
                color = ProgressPink,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            val focusIndex = 2
            val focusWeight = cyanOffsets[focusIndex]
            val focusMuscle = pinkOffsets[focusIndex]

            drawCircle(color = ProgressBgDeep, radius = 8.dp.toPx(), center = focusWeight)
            drawCircle(color = ProgressCyan, radius = 5.dp.toPx(), center = focusWeight)

            drawCircle(color = ProgressBgDeep, radius = 8.dp.toPx(), center = focusMuscle)
            drawCircle(color = ProgressPink, radius = 5.dp.toPx(), center = focusMuscle)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weightPoints.forEachIndexed { index, point ->
                Text(
                    text = point.xLabel,
                    color = if (index == weightPoints.lastIndex) ProgressCyan else ProgressTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (index == weightPoints.lastIndex) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.width(34.dp)
                )
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = ProgressTextPrimary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun PillBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ProgressCyanSoft)
            .border(1.dp, ProgressDivider, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = ProgressCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ProgressBgCardAlt)
            .border(1.dp, achievement.accent.copy(alpha = 0.40f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(achievement.glow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (achievement.accent == ProgressPink) {
                    Icons.Default.LocalFireDepartment
                } else {
                    Icons.Default.TrackChanges
                },
                contentDescription = achievement.title,
                tint = achievement.accent,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = achievement.title,
                color = ProgressTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = achievement.subtitle,
                color = ProgressTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
private fun ProgressScreenPreview() {
    ProgressScreen()
}
