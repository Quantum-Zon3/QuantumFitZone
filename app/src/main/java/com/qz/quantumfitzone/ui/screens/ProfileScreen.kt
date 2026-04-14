package com.qz.quantumfitzone.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.viewModel.ProfileViewModel

// ── Colores del tema ──────────────────────────────────────────────────────────
private val BgDeep       = Color(0xFF080E1A)
private val BgCard       = Color(0xFF0D1726)
private val BgCardAlt    = Color(0xFF0F1C2E)
private val CyanPrimary  = Color(0xFF00D4FF)
private val CyanDim      = Color(0xFF0A8FAA)
private val CyanGlow     = Color(0x3300D4FF)
private val GoldElite    = Color(0xFFFFD700)
private val TextPrimary  = Color(0xFFE8F4FF)
private val TextSecondary= Color(0xFF6B8FAB)
private val DividerColor = Color(0xFF1A2F45)
private val RedSignOut   = Color(0xFFFF3B5C)

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onNavigate: (BottomNavDestination) -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Animación del glow del avatar
    val glowAnim = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowAnim.animateFloat(
        initialValue = 0.4f,
        targetValue  = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        // Fondo degradado sutil en la parte superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A1A30),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // ── Top Bar ───────────────────────────────────────────────────────
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
                        tint = CyanPrimary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Profile",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.weight(1f))
                // Espacio reservado para simetría
                Box(modifier = Modifier.size(48.dp))
            }

            // ── Avatar + Info ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar con glow animado
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    // Glow exterior
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CyanPrimary.copy(alpha = glowAlpha * 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    // Anillo cyan
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, CyanPrimary, CircleShape)
                            .background(BgCard),
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder avatar — reemplazar con AsyncImage cuando tengas Coil
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = CyanDim,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    // Badge verificado
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verified",
                            tint = BgDeep,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = uiState.userName,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Cyber Athlete  ·  Level ${uiState.level}",
                        color = CyanPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Stats Card ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(CyanGlow, Color.Transparent, CyanGlow)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = uiState.workouts.toString(),
                    label = "WORKOUTS",
                    valueColor = TextPrimary
                )
                StatDivider()
                StatItem(
                    value = "${uiState.streakDays}🔥",
                    label = "STREAK",
                    valueColor = TextPrimary
                )
                StatDivider()
                StatItem(
                    value = "⭐ ${uiState.rank}",
                    label = "RANK",
                    valueColor = GoldElite
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Settings Section ──────────────────────────────────────────────
            Text(
                text = "SETTINGS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
            ) {
                SettingsRow(
                    icon = Icons.Outlined.AccountCircle,
                    label = "Account",
                    onClick = viewModel::onNavigateToAccount
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Security,
                    label = "Privacy",
                    onClick = viewModel::onNavigateToPrivacy
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Notifications,
                    label = "Notifications",
                    onClick = viewModel::onNavigateToNotifications
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Devices,
                    label = "Connect Devices",
                    badge = "1 Active",
                    onClick = viewModel::onNavigateToConnectDevices
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Sign Out ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
                    .clickable { onSignOut() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RedSignOut.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = RedSignOut,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Sign Out",
                    color = RedSignOut,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Espacio para que el scroll no quede detrás de la bottom nav
            Spacer(Modifier.height(72.dp))
        }

        // ── Bottom Nav Bar ────────────────────────────────────────────────────
        BottomNavBar(
            selected = BottomNavDestination.PROFILE,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Componentes auxiliares ────────────────────────────────────────────────────

@Composable
private fun StatItem(
    value: String,
    label: String,
    valueColor: Color = TextPrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = valueColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(DividerColor)
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp),
        color = DividerColor,
        thickness = 0.5.dp
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono con fondo
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyanGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CyanPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = label,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Text(
                text = badge,
                color = CyanPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanGlow)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
            Spacer(Modifier.width(6.dp))
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
