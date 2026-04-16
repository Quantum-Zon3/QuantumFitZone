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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.data.model.ExerciseCatalogEntity
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.viewModel.RoutineEditorViewModel
import com.qz.quantumfitzone.viewModel.RoutineExerciseSelection

private val BgDeep = Color(0xFF080E1A)
private val BgCard = Color(0xFF0D1726)
private val BgInput = Color(0xFFFFFFFF)
private val CyanPrimary = Color(0xFF00D4FF)
private val CyanGlow = Color(0x3300D4FF)
private val TextPrimary = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val TextHint = Color(0xFF8FA8BE)
private val DividerColor = Color(0xFF1A2F45)
private val DangerColor = Color(0xFFFF6B81)

private val categories = listOf(
    "Strength & Conditioning",
    "Cardio",
    "Flexibility",
    "HIIT",
    "Recovery"
)

@Composable
fun EditRoutineScreen(
    routineId: Int? = null,
    onNavigateBack: () -> Unit = {},
    onListo: () -> Unit = {},
    viewModel: RoutineEditorViewModel = viewModel()
) {
    val exerciseCatalog by viewModel.exerciseCatalog.collectAsStateWithLifecycle(initialValue = emptyList())
    val machines by viewModel.machines.collectAsStateWithLifecycle(initialValue = emptyList())

    var categoryExpanded by remember { mutableStateOf(false) }
    var showCatalogDialog by rememberSaveable { mutableStateOf(false) }
    var showExerciseFormDialog by rememberSaveable { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<ExerciseCatalogEntity?>(null) }

    LaunchedEffect(routineId, machines) {
        if (routineId != null) {
            viewModel.loadRoutine(routineId, machines)
        } else if (routineId == null && viewModel.currentRoutineId != null) {
            viewModel.prepareNewRoutine()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = CyanPrimary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (routineId == null) "Nueva rutina" else "Editar rutina",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(48.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(CyanPrimary)
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TextSecondary.copy(alpha = 0.4f))
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TextSecondary.copy(alpha = 0.4f))
                )
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel("Routine Name")
            Spacer(Modifier.height(8.dp))
            TextField(
                value = viewModel.routineName,
                onValueChange = viewModel::onRoutineNameChange,
                placeholder = {
                    Text(
                        text = "Cyber Core Workout",
                        color = TextHint,
                        fontSize = 15.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BgInput,
                    unfocusedContainerColor = BgInput,
                    focusedTextColor = BgDeep,
                    unfocusedTextColor = BgDeep,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            viewModel.routineError?.let { error ->
                Text(
                    text = error,
                    color = DangerColor,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel("Category")
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BgCard)
                        .clickable { categoryExpanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.selectedCategory,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = CyanPrimary
                    )
                }
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(BgCard)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category, color = TextPrimary) },
                            onClick = {
                                viewModel.onCategoryChange(category)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Exercises",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${viewModel.selectedExercises.size} seleccionados para esta rutina",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "+ Add New",
                    color = CyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showCatalogDialog = true }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (viewModel.selectedExercises.isEmpty()) {
                EmptyExerciseSelectionCard(
                    onOpenCatalog = { showCatalogDialog = true }
                )
            } else {
                viewModel.selectedExercises.forEachIndexed { index, exercise ->
                    RoutineExerciseRow(
                        exercise = exercise,
                        onPesoActualChange = { viewModel.updateExercisePesoActual(exercise.exerciseId, it) },
                        onPesoObjetivoChange = { viewModel.updateExercisePesoObjetivo(exercise.exerciseId, it) },
                        onDelete = { viewModel.removeExerciseFromRoutine(exercise.exerciseId) }
                    )
                    if (index < viewModel.selectedExercises.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = DividerColor,
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Default Rest Time",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${viewModel.restTimeSeconds.toInt()}s",
                    color = CyanPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Slider(
                value = viewModel.restTimeSeconds,
                onValueChange = viewModel::onRestTimeChange,
                valueRange = 15f..180f,
                modifier = Modifier.padding(horizontal = 20.dp),
                colors = SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = CyanPrimary,
                    inactiveTrackColor = DividerColor
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15s", color = TextSecondary, fontSize = 11.sp)
                Text("90s", color = TextSecondary, fontSize = 11.sp)
                Text("180s", color = TextSecondary, fontSize = 11.sp)
            }

            viewModel.saveMessage?.let { message ->
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyanGlow)
                        .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = message,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }

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
                    .clickable {
                        viewModel.saveRoutine {
                            onListo()
                        }
                    }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Listo",
                    color = BgDeep,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BgDeep,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showCatalogDialog) {
        ExerciseCatalogDialog(
            catalog = exerciseCatalog,
            machines = machines,
            selectedExerciseIds = viewModel.selectedExercises.map { it.exerciseId }.toSet(),
            onDismiss = { showCatalogDialog = false },
            onCreateExercise = {
                viewModel.prepareCreateExercise()
                showExerciseFormDialog = true
            },
            onAddExercise = { exercise ->
                viewModel.addExerciseToRoutine(exercise, machines)
            },
            onEditExercise = { exercise ->
                viewModel.prepareEditExercise(exercise)
                showExerciseFormDialog = true
            },
            onDeleteExercise = { exercise ->
                exerciseToDelete = exercise
            }
        )
    }

    if (showExerciseFormDialog) {
        ExerciseFormDialog(
            viewModel = viewModel,
            machines = machines,
            onDismiss = { showExerciseFormDialog = false },
            onSaveSuccess = { savedExercise ->
                viewModel.addExerciseToRoutine(savedExercise, machines)
                showExerciseFormDialog = false
                showCatalogDialog = true
            }
        )
    }

    exerciseToDelete?.let { exercise ->
        DeleteExerciseDialog(
            exercise = exercise,
            onDismiss = { exerciseToDelete = null },
            onConfirm = {
                viewModel.deleteExerciseCatalog(exercise)
                exerciseToDelete = null
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = CyanPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun EmptyExerciseSelectionCard(onOpenCatalog: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .border(1.dp, DividerColor, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "Todavia no has agregado ejercicios",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Abre el catalogo para reutilizar ejercicios existentes o crear uno nuevo sin depender de una maquina.",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onOpenCatalog,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = BgDeep
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Abrir catalogo")
        }
    }
}

@Composable
private fun RoutineExerciseRow(
    exercise: RoutineExerciseSelection,
    onPesoActualChange: (String) -> Unit,
    onPesoObjetivoChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .heightIn(min = 70.dp)
            .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyanGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.nombre,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = buildExerciseDetail(exercise.grupoMuscular, exercise.machineName),
                color = TextSecondary,
                fontSize = 12.sp
            )
            exercise.descripcion?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    color = TextSecondary.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            buildExerciseStats(exercise.series, exercise.repeticiones)?.let { stats ->
                Text(
                    text = stats,
                    color = CyanPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = exercise.pesoActual,
                    onValueChange = onPesoActualChange,
                    label = { Text("Peso actual") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                OutlinedTextField(
                    value = exercise.pesoObjetivo,
                    onValueChange = onPesoObjetivoChange,
                    label = { Text("Peso objetivo") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Quitar",
                tint = TextSecondary
            )
        }
    }
}

private fun buildExerciseDetail(group: String?, machineName: String?): String {
    val parts = listOfNotNull(
        group?.takeIf { it.isNotBlank() },
        machineName ?: "Sin maquina"
    )
    return parts.joinToString(" • ")
}

private fun buildExerciseStats(series: Int?, repeticiones: Int?): String? {
    val parts = listOfNotNull(
        series?.let { "$it series" },
        repeticiones?.let { "$it reps" }
    )
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

@Composable
private fun ExerciseCatalogDialog(
    catalog: List<ExerciseCatalogEntity>,
    machines: List<MaquinaEntity>,
    selectedExerciseIds: Set<Int>,
    onDismiss: () -> Unit,
    onCreateExercise: () -> Unit,
    onAddExercise: (ExerciseCatalogEntity) -> Unit,
    onEditExercise: (ExerciseCatalogEntity) -> Unit,
    onDeleteExercise: (ExerciseCatalogEntity) -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }

    val filteredCatalog = remember(catalog, search) {
        val query = search.trim().lowercase()
        if (query.isBlank()) {
            catalog
        } else {
            catalog.filter { exercise ->
                exercise.nombre.lowercase().contains(query) ||
                    exercise.descripcion.orEmpty().lowercase().contains(query) ||
                    exercise.grupo_muscular.orEmpty().lowercase().contains(query)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard,
        title = {
            Column {
                Text(
                    text = "Catalogo de ejercicios",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Crea una vez y reutiliza en cualquier rutina",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    label = { Text("Buscar ejercicio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onCreateExercise,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = BgDeep
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Crear ejercicio nuevo")
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredCatalog.isEmpty()) {
                        Text(
                            text = "No hay ejercicios que coincidan con la busqueda.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    } else {
                        filteredCatalog.forEach { exercise ->
                            val machineName = machines.firstOrNull {
                                it.id_maquina == exercise.id_maquina
                            }?.nombre

                            ExerciseCatalogItem(
                                exercise = exercise,
                                machineName = machineName,
                                isSelected = selectedExerciseIds.contains(exercise.id_exercise),
                                onAdd = { onAddExercise(exercise) },
                                onEdit = { onEditExercise(exercise) },
                                onDelete = { onDeleteExercise(exercise) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = CyanPrimary)
            }
        },
        dismissButton = {}
    )
}

@Composable
private fun ExerciseCatalogItem(
    exercise: ExerciseCatalogEntity,
    machineName: String?,
    isSelected: Boolean,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF102032))
            .border(1.dp, DividerColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.nombre,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = buildExerciseDetail(exercise.grupo_muscular, machineName),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                buildExerciseStats(exercise.series, exercise.repeticiones)?.let { stats ->
                    Text(
                        text = stats,
                        color = CyanPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            if (isSelected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Agregado",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = BgDeep
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Agregar")
                }
            }
        }

        exercise.descripcion?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionMiniButton(
                label = "Editar",
                icon = Icons.Default.Edit,
                tint = CyanPrimary,
                onClick = onEdit
            )
            ActionMiniButton(
                label = "Eliminar",
                icon = Icons.Default.DeleteOutline,
                tint = DangerColor,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun ActionMiniButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ExerciseFormDialog(
    viewModel: RoutineEditorViewModel,
    machines: List<MaquinaEntity>,
    onDismiss: () -> Unit,
    onSaveSuccess: (ExerciseCatalogEntity) -> Unit
) {
    val form = viewModel.exerciseForm
    val title = if (viewModel.isEditingExercise) "Editar ejercicio" else "Nuevo ejercicio"
    var machineExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard,
        title = {
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "El ejercicio se guarda en un catalogo reutilizable.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = form.nombre,
                    onValueChange = viewModel::onExerciseNameChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = form.grupo_muscular.orEmpty(),
                    onValueChange = viewModel::onExerciseGroupChange,
                    label = { Text("Grupo muscular") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = form.descripcion.orEmpty(),
                    onValueChange = viewModel::onExerciseDescriptionChange,
                    label = { Text("Descripcion") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = form.series?.toString().orEmpty(),
                        onValueChange = viewModel::onExerciseSeriesChange,
                        label = { Text("Series") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = form.repeticiones?.toString().orEmpty(),
                        onValueChange = viewModel::onExerciseRepeticionesChange,
                        label = { Text("Repeticiones") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Column {
                    Text(
                        text = "Maquina asociada",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF102032))
                                .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
                                .clickable { machineExpanded = true }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = machines.firstOrNull { it.id_maquina == form.id_maquina }?.nombre
                                    ?: "Sin maquina",
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = CyanPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = machineExpanded,
                            onDismissRequest = { machineExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin maquina") },
                                onClick = {
                                    viewModel.onExerciseMachineChange(null)
                                    machineExpanded = false
                                }
                            )
                            machines.forEach { machine ->
                                DropdownMenuItem(
                                    text = { Text(machine.nombre) },
                                    onClick = {
                                        viewModel.onExerciseMachineChange(machine.id_maquina)
                                        machineExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                viewModel.exerciseFormError?.let { error ->
                    Text(
                        text = error,
                        color = DangerColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.upsertExerciseCatalog(machines) { saved ->
                        onSaveSuccess(saved)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = BgDeep
                )
            ) {
                Text(if (viewModel.isEditingExercise) "Guardar cambios" else "Crear ejercicio")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun DeleteExerciseDialog(
    exercise: ExerciseCatalogEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard,
        title = {
            Text(
                text = "Eliminar ejercicio",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Se eliminara \"${exercise.nombre}\" del catalogo reusable. Si estaba en la rutina actual, tambien se quitara de la seleccion.",
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerColor,
                    contentColor = Color.White
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun EditRoutineScreenPreview() {
    EditRoutineScreen()
}
