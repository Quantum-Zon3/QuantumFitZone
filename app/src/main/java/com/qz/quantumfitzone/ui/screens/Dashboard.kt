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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.qz.quantumfitzone.viewModel.HistorialEntrenamientoViewModel
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val BgDeep = Color(0xFF080E1A)
private val BgCard = Color(0xFF0D1726)
private val BgCardAlt = Color(0xFF0F1C2E)
private val CyanPrimary = Color(0xFF00D4FF)
private val CyanGlow = Color(0x3300D4FF)
private val TextPrimary = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DividerColor = Color(0xFF1A2F45)

@Composable
fun DashboardScreen(
    onNavigate: (BottomNavDestination) -> Unit = {},
    onStartRoutine: () -> Unit = {},
    viewModel: HistorialEntrenamientoViewModel = viewModel()
) {
    val activeSessionState by viewModel.sesionActivaUiState.collectAsStateWithLifecycle()
    var elapsedSeconds by remember(activeSessionState.startedAt) { mutableLongStateOf(0L) }

    LaunchedEffect(activeSessionState.hasActiveSession, activeSessionState.startedAt) {
        if (!activeSessionState.hasActiveSession || activeSessionState.startedAt == null) {
            elapsedSeconds = 0L
            return@LaunchedEffect
        }

        while (true) {
            elapsedSeconds = calculateElapsedSeconds(activeSessionState.startedAt)
            delay(1000)
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
                .padding(bottom = 100.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Dashboard",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Today's Session",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (activeSessionState.hasActiveSession) {
                        "You already have a routine in progress."
                    } else {
                        "Start one of your saved routines and continue from here."
                    },
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            if (activeSessionState.isLoading) {
                DashboardMessageCard(
                    title = "Loading routine status",
                    message = "Checking if you already started a routine today."
                )
            } else if (activeSessionState.hasActiveSession) {
                ActiveRoutineCard(
                    title = activeSessionState.routineTitle,
                    category = activeSessionState.category,
                    date = activeSessionState.date,
                    elapsedTime = formatElapsedTime(elapsedSeconds)
                )
            } else {
                StartRoutineCard(onStartRoutine = onStartRoutine)
            }

            Spacer(Modifier.height(18.dp))

            DashboardMessageCard(
                title = "What comes next",
                message = "In the next block we will load the active session exercises here, so you can complete the routine from this dashboard."
            )
        }

        BottomNavBar(
            selected = BottomNavDestination.DASHBOARD,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ActiveRoutineCard(
    title: String,
    category: String,
    date: String,
    elapsedTime: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "ACTIVE ROUTINE",
            color = CyanPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SessionBadge(label = category.ifBlank { "Routine" })
            SessionBadge(label = date)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Elapsed time",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = elapsedTime,
            color = CyanPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "The exercise tracker and finish action will appear in the next steps.",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun StartRoutineCard(onStartRoutine: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "NO ACTIVE ROUTINE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "You have not started a routine yet.",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Choose one from your saved routines to begin today's training session.",
            color = TextSecondary,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onStartRoutine,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = BgDeep
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Start Routine",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DashboardMessageCard(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BgCardAlt)
            .border(1.dp, DividerColor, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 15.sp,
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
private fun SessionBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CyanGlow)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = CyanPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}

private fun calculateElapsedSeconds(startedAt: String?): Long {
    if (startedAt.isNullOrBlank()) return 0L

    val start = runCatching {
        LocalDateTime.parse(startedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }.getOrNull() ?: return 0L

    return Duration.between(start, LocalDateTime.now()).seconds.coerceAtLeast(0)
}

private fun formatElapsedTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
