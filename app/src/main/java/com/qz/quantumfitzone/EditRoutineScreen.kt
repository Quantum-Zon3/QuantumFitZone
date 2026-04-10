package com.qz.quantumfitzone

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// ── Colores ───────────────────────────────────────────────────────────────────
private val BgDeep        = Color(0xFF080E1A)
private val BgCard        = Color(0xFF0D1726)
private val BgInput       = Color(0xFFFFFFFF)
private val CyanPrimary   = Color(0xFF00D4FF)
private val CyanGlow      = Color(0x3300D4FF)
private val TextPrimary   = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val TextHint      = Color(0xFF8FA8BE)
private val DividerColor  = Color(0xFF1A2F45)

// ── Modelo ────────────────────────────────────────────────────────────────────
private data class Exercise(
    val name: String,
    val detail: String,
    val icon: ImageVector
)

private val sampleExercises = listOf(
    Exercise("Holographic Deadlifts", "4 sets x 10 reps", Icons.Default.FitnessCenter),
    Exercise("Zero-G Pullups",        "3 sets x 12 reps", Icons.Default.SelfImprovement),
    Exercise("Quantum Sprints",       "5 sets x 30s",     Icons.Default.DirectionsRun)
)

private val categories = listOf(
    "Strength & Conditioning",
    "Cardio",
    "Flexibility",
    "HIIT",
    "Recovery"
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun EditRoutineScreen(
    onNavigateBack: () -> Unit = {},
    onListo: () -> Unit = {},
) {
    var routineName  by remember { mutableStateOf("") }
    var restTime     by remember { mutableStateOf(60f) }
    var expanded     by remember { mutableStateOf(false) }
    var selectedCat  by remember { mutableStateOf(categories[0]) }
    val exercises    = remember { mutableStateListOf(*sampleExercises.toTypedArray()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onNavigateBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = CyanPrimary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text          = "Edit Routine",
                    color         = TextPrimary,
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(48.dp))
            }

            // ── Step indicators ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step 1 activo
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(CyanPrimary)
                )
                Spacer(Modifier.width(6.dp))
                // Step 2
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TextSecondary.copy(alpha = 0.4f))
                )
                Spacer(Modifier.width(6.dp))
                // Step 3
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TextSecondary.copy(alpha = 0.4f))
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Routine Name ──────────────────────────────────────────────────
            Text(
                text          = "Routine Name",
                color         = CyanPrimary,
                fontSize      = 13.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                modifier      = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value         = routineName,
                onValueChange = { routineName = it },
                placeholder   = {
                    Text(
                        text  = "Cyber Core Workout",
                        color = TextHint,
                        fontSize = 15.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = BgInput,
                    unfocusedContainerColor = BgInput,
                    focusedTextColor        = BgDeep,
                    unfocusedTextColor      = BgDeep,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // ── Category ──────────────────────────────────────────────────────
            Text(
                text          = "Category",
                color         = CyanPrimary,
                fontSize      = 13.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                modifier      = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BgCard)
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text     = selectedCat,
                        color    = TextPrimary,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector        = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint               = CyanPrimary
                    )
                }
                DropdownMenu(
                    expanded         = expanded,
                    onDismissRequest = { expanded = false },
                    modifier         = Modifier.background(BgCard)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text    = { Text(cat, color = TextPrimary, fontSize = 14.sp) },
                            onClick = { selectedCat = cat; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Exercises ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Exercises",
                    color      = TextPrimary,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.weight(1f)
                )
                Text(
                    text      = "+ Add New",
                    color     = CyanPrimary,
                    fontSize  = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier  = Modifier.clickable { /* próximamente */ }
                )
            }

            Spacer(Modifier.height(12.dp))

            exercises.forEachIndexed { index, exercise ->
                ExerciseRow(
                    exercise = exercise,
                    onDelete = { exercises.removeAt(index) }
                )
                if (index < exercises.lastIndex) {
                    HorizontalDivider(
                        modifier  = Modifier.padding(horizontal = 20.dp),
                        color     = DividerColor,
                        thickness = 0.5.dp
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Default Rest Time ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Default Rest Time",
                    color      = TextPrimary,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.weight(1f)
                )
                Text(
                    text       = "${restTime.toInt()}s",
                    color      = CyanPrimary,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Slider(
                value         = restTime,
                onValueChange = { restTime = it },
                valueRange    = 15f..180f,
                modifier      = Modifier.padding(horizontal = 20.dp),
                colors        = SliderDefaults.colors(
                    thumbColor          = CyanPrimary,
                    activeTrackColor    = CyanPrimary,
                    inactiveTrackColor  = DividerColor
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15s",  color = TextSecondary, fontSize = 11.sp)
                Text("90s",  color = TextSecondary, fontSize = 11.sp)
                Text("180s", color = TextSecondary, fontSize = 11.sp)
            }
        }

        // ── Botón Listo ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BgDeep)
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(CyanPrimary)
                    .clickable { onListo() }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Listo",
                    color      = BgDeep,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector        = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint               = BgDeep,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Exercise Row ──────────────────────────────────────────────────────────────
@Composable
private fun ExerciseRow(
    exercise: Exercise,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .heightIn(60.dp)
            .border(1.dp, CyanPrimary, CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Drag handle
        Icon(
            imageVector        = Icons.Default.DragHandle,
            contentDescription = null,
            tint               = TextSecondary.copy(alpha = 0.5f),
            modifier           = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        // Icono ejercicio
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyanGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = exercise.icon,
                contentDescription = null,
                tint               = CyanPrimary,
                modifier           = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        // Nombre y detalle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = exercise.name,
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text     = exercise.detail,
                color    = TextSecondary,
                fontSize = 12.sp
            )
        }
        // Eliminar
        IconButton(onClick = onDelete) {
            Icon(
                imageVector        = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint               = TextSecondary.copy(alpha = 0.6f),
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun EditRoutineScreenPreview() {
    EditRoutineScreen()
}