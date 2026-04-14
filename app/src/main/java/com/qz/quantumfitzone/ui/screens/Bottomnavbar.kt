package com.qz.quantumfitzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDeep         = Color(0xFF080E1A)
private val CyanPrimary    = Color(0xFF00D4FF)
private val TextSecondary  = Color(0xFF6B8FAB)

// ── Destinos ──────────────────────────────────────────────────────────────────
enum class BottomNavDestination {
    DASHBOARD, ROUTINES, HISTORY, PROGRESS, PROFILE, SUPPORT, HOME, USERS, MACHINES
}

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val destination: BottomNavDestination
)

private val navItems = listOf(
    NavItem("Dashboard", Icons.Default.Dashboard, BottomNavDestination.DASHBOARD),
    NavItem("Routines", Icons.Default.FitnessCenter,  BottomNavDestination.ROUTINES),
    NavItem("History",  Icons.Default.History,        BottomNavDestination.HISTORY),
    NavItem("Progress", Icons.Default.BarChart,       BottomNavDestination.PROGRESS),
    NavItem("Profile",  Icons.Default.Person,         BottomNavDestination.PROFILE),
    NavItem("Support",  Icons.Default.HeadsetMic,     BottomNavDestination.SUPPORT)
)

private val navItemsAdmin = listOf(
    NavItem("Home", Icons.Default.Home, BottomNavDestination.HOME),
    NavItem("Users", Icons.Default.Person, BottomNavDestination.USERS),
    NavItem("Machines", Icons.Default.FitnessCenter, BottomNavDestination.MACHINES)
)

// ── Composable ────────────────────────────────────────────────────────────────
@Composable
fun BottomNavBar(
    selected: BottomNavDestination,
    onItemClick: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, BgDeep.copy(alpha = 0.95f)),
                    startY = 0f,
                    endY   = 40f
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A1520).copy(alpha = 0.97f))
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            navItems.forEach { item ->
                NavBarItem(
                    item       = item,
                    isSelected = item.destination == selected,
                    onClick    = { onItemClick(item.destination) }
                )
            }
        }
    }
}

@Composable
fun BottomNavBarAdmin(
    selected: BottomNavDestination,
    onItemClick: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, BgDeep.copy(alpha = 0.95f)),
                    startY = 0f,
                    endY   = 40f
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A1520).copy(alpha = 0.97f))
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            navItemsAdmin.forEach { item ->
                NavBarItem(
                    item       = item,
                    isSelected = item.destination == selected,
                    onClick    = { onItemClick(item.destination) }
                )
            }
        }
    }
}
@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = item.label,
            tint               = if (isSelected) CyanPrimary else TextSecondary,
            modifier           = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text          = item.label,
            fontSize      = 10.sp,
            fontWeight    = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color         = if (isSelected) CyanPrimary else TextSecondary,
            letterSpacing = 0.3.sp
        )
        if (isSelected) {
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
            )
        }
    }
}