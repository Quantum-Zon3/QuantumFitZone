package com.qz.quantumfitzone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ActivityItem(title: String, subtitle: String, xp: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2A3A))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(title, color = Color.White)
                Text(subtitle, color = Color.Gray)
            }

            Text(xp, color = Color.Cyan)
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2A3A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = Color.Cyan)
            Column {
                Text(value, color = Color.White)
                Text(label, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DashboardScreen(
    onNavigate: (BottomNavDestination) -> Unit = {},
) {

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0F1C),
            Color(0xFF081520),
            Color(0xFF06202A)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {

            Text(
                text = "User Dashboard",
                color = Color.LightGray,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text("Good Morning, Neo", color = Color.White)
                    }
                }

                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.Cyan
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 ACTIVE ROUTINE CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0D2A3A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("ACTIVE ROUTINE", color = Color.Cyan)
                    Text("Neon Night Run", color = Color.White)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Time 12:00 / 45:00", color = Color.LightGray)
                        Text("135 bpm", color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 STATS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                StatCard("342", "kcal", Icons.Default.LocalFireDepartment)
                StatCard("1h 12m", "Duration", Icons.Default.Timer)
                StatCard("145", "Avg bpm", Icons.Default.Favorite)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Recent Activity", color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 ACTIVITY ITEMS
            ActivityItem("Cyber Strength V.2", "Yesterday · 45 mins", "+200 XP")
            ActivityItem("Zen Matrix Flow", "2 days ago · 30 mins", "+150 XP")


        }
        BottomNavBar(
            selected = BottomNavDestination.DASHBOARD,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}