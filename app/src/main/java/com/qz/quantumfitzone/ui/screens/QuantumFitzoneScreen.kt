package com.example.quantumfitzone

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.viewModel.PersonaViewModel

// ─── Colors ──────────────────────────────────────────────────────────────────

private val BackgroundDeep  = Color(0xFF0A0B1A)
private val BackgroundCard  = Color(0xFF111328)
private val BorderCard      = Color(0xFF1E2240)
private val AccentCyan      = Color(0xFF00E5FF)
private val AccentPurple    = Color(0xFF7B2FFF)
private val AccentPurpleEnd = Color(0xFF9B59FF)
private val TextPrimary     = Color(0xFFFFFFFF)
private val TextSecondary   = Color(0xFF8A8FAD)

// ─── Main Screen ─────────────────────────────────────────────────────────────

@Composable
fun QuantumFitzoneScreen(
    context: Context = LocalContext.current,
    viewModel: PersonaViewModel = viewModel(),
    onRegistro: () -> Unit = {},
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    onDashboard: () -> Unit = {},
    onDashboardAdmin: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.cargarDatos(
            context = context,
            onClickDashboardUsuario = { onDashboard() },
            onClickDashboardAdmin = { onDashboardAdmin() }
        )
        viewModel.admin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {

        // Subtle radial glow behind the dumbbell icon
        GlowBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Top bar ──────────────────────────────────────────────────────
            TopBar(onBack = onBack)

            Spacer(modifier = Modifier.height(40.dp))

            // ── Dumbbell icon ─────────────────────────────────────────────
            DumbbellIcon()

            Spacer(modifier = Modifier.height(32.dp))

            // ── Headline ──────────────────────────────────────────────────
            Text(
                text = "LEVEL UP\nYOUR FITNESS",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Subtitle ──────────────────────────────────────────────────
            Text(
                text = "Experience the future of personal training\nwith AI-driven routines and real-time\nhologram tracking.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Feature cards ─────────────────────────────────────────────
            FeatureCard(
                icon = Icons.Default.SmartToy,
                title = "Smart Routines",
                subtitle = "AI-powered workout plans tailored for you."
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureCard(
                icon = Icons.Default.BarChart,
                title = "Live Tracking",
                subtitle = "Track your performance in real-time."
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── CTA buttons ───────────────────────────────────────────────
            StartTrainingButton(onClick = onRegistro)

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onLogin) {
                Text(
                    text = "Login",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun TopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }
        Text(
            text = "Quantum Fitzone",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// ─── Glow Background ─────────────────────────────────────────────────────────

@Composable
private fun GlowBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentCyan.copy(alpha = glowAlpha),
                            AccentPurple.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height * 0.35f),
                        radius = size.width * 0.65f
                    )
                )
            }
    )
}

// ─── Dumbbell Icon ───────────────────────────────────────────────────────────

@Composable
private fun DumbbellIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1F45),
                        Color(0xFF0D1030)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(AccentCyan.copy(alpha = 0.6f), AccentPurple.copy(alpha = 0.4f))
                ),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = "Dumbbell",
            tint = AccentCyan,
            modifier = Modifier.size(46.dp)
        )
    }
}

// ─── Feature Card ─────────────────────────────────────────────────────────────

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .border(
                width = 1.dp,
                color = BorderCard,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Icon circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(AccentPurple.copy(alpha = 0.3f), AccentCyan.copy(alpha = 0.15f))
                    )
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

// ─── Start Training Button ───────────────────────────────────────────────────

@Composable
private fun StartTrainingButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(AccentPurple, AccentPurpleEnd)
                    ),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Text(
                text = "Start Training",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

