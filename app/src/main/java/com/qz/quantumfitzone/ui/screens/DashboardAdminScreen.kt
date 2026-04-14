package com.qz.quantumfitzone.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colores (misma paleta que SupportScreen) ─────────────────────────────────
private val BgDeep        = Color(0xFF080E1A)
private val BgCard        = Color(0xFF0D1726)
private val CyanPrimary   = Color(0xFF00D4FF)
private val CyanGlow      = Color(0x3300D4FF)
private val CyanSoft      = Color(0x1A00D4FF)
private val TextPrimary   = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DividerColor  = Color(0xFF1A2F45)
private val GreenPositive = Color(0xFF00E676)
private val CardBorder    = Color(0xFF132030)

// ── Datos del gráfico ─────────────────────────────────────────────────────────
private val chartPoints = listOf(
    Pair("Jan", 0.30f),
    Pair("Feb", 0.42f),
    Pair("Mar", 0.38f),
    Pair("Apr", 0.55f),
    Pair("May", 0.65f),
    Pair("Jun", 0.90f)
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun DashboardScreenAdmin(
    onNavigate: (BottomNavDestination) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 150.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Menu icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgCard)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint               = CyanPrimary,
                        modifier           = Modifier.size(20.dp)
                    )
                }

                Text(
                    text          = "Dashboard",
                    color         = TextPrimary,
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                // Notification icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgCard)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint               = CyanPrimary,
                        modifier           = Modifier.size(20.dp)
                    )
                    // Dot indicador
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary)
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                    )
                }
            }

            // ── Welcome Header ────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(
                    text       = "Welcome, Admin",
                    color      = TextPrimary,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GreenPositive)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text     = "System is running optimally",
                        color    = GreenPositive,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Stat Cards ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Default.People,
                    label       = "Total Users",
                    value       = "2,450",
                    change      = "+10% this week",
                    changeColor = GreenPositive
                )
                StatCard(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Default.FlashOn,
                    label       = "Active Subs",
                    value       = "1,890",
                    change      = "+5% this month",
                    changeColor = GreenPositive
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Action Buttons ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Broadcast button (filled)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyanPrimary)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Campaign,
                            contentDescription = null,
                            tint               = BgDeep,
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = "Broadcast",
                            color      = BgDeep,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Logs button (outlined)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgCard)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Assignment,
                            contentDescription = null,
                            tint               = TextSecondary,
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text     = "Logs",
                            color    = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── User Growth Chart ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
                    .padding(20.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text       = "User Growth",
                            color      = TextPrimary,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text     = "+450 registrations this month",
                            color    = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    // Period selector pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyanSoft)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text     = "Months",
                            color    = CyanPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Canvas chart
                LineChartCanvas(
                    points   = chartPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Section label ──────────────────────────────────────────────────
            Text(
                text          = "QUICK STATS",
                color         = CyanPrimary,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier      = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ── Quick Stats list ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
            ) {
                QuickStatRow(
                    icon    = Icons.Default.TrendingUp,
                    label   = "Sessions today",
                    value   = "1,234",
                    showDivider = true
                )
                QuickStatRow(
                    icon    = Icons.Default.Timer,
                    label   = "Avg. session time",
                    value   = "47 min",
                    showDivider = true
                )
                QuickStatRow(
                    icon    = Icons.Default.Star,
                    label   = "Satisfaction score",
                    value   = "4.8 / 5",
                    showDivider = false
                )
            }
        }

        // ── Bottom Nav ────────────────────────────────────────────────────────
        BottomNavBarAdmin(
            selected    = BottomNavDestination.HOME,
            onItemClick = onNavigate,
            modifier    = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Stat Card ─────────────────────────────────────────────────────────────────
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    change: String,
    changeColor: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text     = label,
                color    = TextSecondary,
                fontSize = 12.sp
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = CyanPrimary,
                    modifier           = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text       = value,
            color      = TextPrimary,
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text     = change,
            color    = changeColor,
            fontSize = 11.sp
        )
    }
}

// ── Quick Stat Row ────────────────────────────────────────────────────────────
@Composable
private fun QuickStatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyanGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = CyanPrimary,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text     = label,
                color    = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text       = value,
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(horizontal = 16.dp),
                color     = DividerColor,
                thickness = 0.5.dp
            )
        }
    }
}

// ── Line Chart Canvas ─────────────────────────────────────────────────────────
@Composable
private fun LineChartCanvas(
    points: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val animProgress by animateFloatAsState(
        targetValue    = 1f,
        animationSpec  = tween(durationMillis = 1200, easing = EaseOutCubic),
        label          = "chartAnim"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padBottom = 28f
        val padTop    = 10f
        val chartH    = h - padBottom - padTop

        val stepX = w / (points.size - 1)

        // Calcular posiciones
        val coords = points.mapIndexed { i, (_, v) ->
            Offset(
                x = i * stepX,
                y = padTop + chartH * (1f - v)
            )
        }

        val animatedCoords = coords.map { pt ->
            pt.copy(y = padTop + chartH - (padTop + chartH - pt.y) * animProgress)
        }

        // Fill bajo la línea
        val fillPath = Path().apply {
            moveTo(animatedCoords.first().x, h - padBottom)
            animatedCoords.forEach { lineTo(it.x, it.y) }
            lineTo(animatedCoords.last().x, h - padBottom)
            close()
        }
        drawPath(
            path  = fillPath,
            brush = Brush.verticalGradient(
                colors     = listOf(Color(0x5500D4FF), Color(0x0000D4FF)),
                startY     = padTop,
                endY       = h - padBottom
            )
        )

        // Línea principal
        val linePath = Path().apply {
            animatedCoords.forEachIndexed { i, pt ->
                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }
        drawPath(
            path   = linePath,
            color  = Color(0xFF00D4FF),
            style  = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Puntos
        animatedCoords.forEach { pt ->
            drawCircle(color = Color(0xFF00D4FF), radius = 5f, center = pt)
            drawCircle(color = Color(0xFF080E1A), radius = 2.5f, center = pt)
        }

        // Labels del eje X
        val paint = android.graphics.Paint().apply {
            color     = android.graphics.Color.parseColor("#6B8FAB")
            textSize  = 28f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        points.forEachIndexed { i, (label, _) ->
            drawContext.canvas.nativeCanvas.drawText(
                label,
                i * stepX,
                h,
                paint
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun DashboardScreenAdminPreview() {
    DashboardScreenAdmin()
}