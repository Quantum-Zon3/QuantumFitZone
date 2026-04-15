package com.qz.quantumfitzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.viewModel.MaquinaViewModel

private val AdminBgDeep = Color(0xFF08111D)
private val AdminBgCard = Color(0xFF0D1A28)
private val AdminBgCardAlt = Color(0xFF102032)
private val AdminCyan = Color(0xFF00D4FF)
private val AdminCyanSoft = Color(0x3300D4FF)
private val AdminTextPrimary = Color(0xFFE8F4FF)
private val AdminTextSecondary = Color(0xFF7A99B3)
private val AdminDivider = Color(0xFF17334B)
private val AdminDanger = Color(0xFFFF5C7A)
private val AdminDangerSoft = Color(0x33FF5C7A)

@Composable
fun AdminMachinesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigate: (BottomNavDestination) -> Unit = {},
    viewModel: MaquinaViewModel = viewModel()
) {
    val maquinas by viewModel.maquinas.collectAsStateWithLifecycle(initialValue = emptyList())
    var filtroGrupo by rememberSaveable { mutableStateOf("Todas") }
    var busqueda by rememberSaveable { mutableStateOf("") }
    var showForm by rememberSaveable { mutableStateOf(false) }
    var maquinaAEliminar by remember { mutableStateOf<MaquinaEntity?>(null) }

    val grupos = remember(maquinas) {
        buildList {
            add("Todas")
            maquinas
                .mapNotNull { it.grupo_muscular?.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .sorted()
                .forEach(::add)
        }
    }

    val maquinasFiltradas = maquinas.filter { maquina ->
        val coincideGrupo = filtroGrupo == "Todas" || maquina.grupo_muscular == filtroGrupo
        val texto = busqueda.trim().lowercase()
        val coincideBusqueda = texto.isBlank() ||
            maquina.nombre.lowercase().contains(texto) ||
            maquina.grupo_muscular.orEmpty().lowercase().contains(texto) ||
            maquina.descripcion.orEmpty().lowercase().contains(texto)
        coincideGrupo && coincideBusqueda
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBgDeep)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A1A30), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AdminTopBar(
                onNavigateBack = onNavigateBack,
                onCreateClick = {
                    viewModel.prepareCreate()
                    showForm = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SearchField(
                value = busqueda,
                onValueChange = { busqueda = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                grupos.forEach { grupo ->
                    FilterChip(
                        label = grupo,
                        selected = filtroGrupo == grupo,
                        onClick = { filtroGrupo = grupo }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SummaryRow(total = maquinas.size, visibles = maquinasFiltradas.size)

            Spacer(modifier = Modifier.height(14.dp))

            if (maquinasFiltradas.isEmpty()) {
                EmptyMachinesCard(
                    onCreateClick = {
                        viewModel.prepareCreate()
                        showForm = true
                    }
                )
            } else {
                maquinasFiltradas.forEach { maquina ->
                    MachineCard(
                        maquina = maquina,
                        onEdit = {
                            viewModel.prepareEdit(maquina)
                            showForm = true
                        },
                        onDelete = { maquinaAEliminar = maquina }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        BottomNavBarAdmin(
            selected = BottomNavDestination.MACHINES,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showForm) {
        MachineFormDialog(
            viewModel = viewModel,
            onDismiss = {
                viewModel.clearForm()
                showForm = false
            },
            onSaveSuccess = { showForm = false }
        )
    }

    maquinaAEliminar?.let { maquina ->
        DeleteMachineDialog(
            maquina = maquina,
            onDismiss = { maquinaAEliminar = null },
            onConfirm = {
                viewModel.eliminar(maquina)
                maquinaAEliminar = null
            }
        )
    }
}

@Composable
private fun AdminTopBar(
    onNavigateBack: () -> Unit,
    onCreateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = AdminTextPrimary
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Manage Machines",
                color = AdminCyan,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Administra el catalogo interno del gimnasio",
                color = AdminTextSecondary,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AdminCyan,
                contentColor = AdminBgDeep
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Nueva")
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AdminTextSecondary
            )
        },
        label = { Text("Buscar maquina") },
        placeholder = { Text("Nombre, grupo muscular o descripcion") },
        singleLine = true,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AdminCyanSoft else AdminBgCardAlt)
            .border(
                width = 1.dp,
                color = if (selected) AdminCyan else AdminDivider,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) AdminCyan else AdminTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun SummaryRow(total: Int, visibles: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Catalogo de maquinas",
            color = AdminTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$visibles de $total",
            color = AdminCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MachineCard(
    maquina: MaquinaEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AdminBgCard)
            .border(1.dp, AdminDivider, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MachineAvatar(
                nombre = maquina.nombre,
                hasImage = !maquina.imagen.isNullOrBlank()
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = maquina.nombre,
                    color = AdminTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = maquina.grupo_muscular ?: "Sin grupo asignado",
                    color = AdminCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = maquina.descripcion ?: "Sin descripcion registrada.",
                    color = AdminTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MachineIdBadge(id = maquina.id_maquina)
        }

        if (!maquina.imagen.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = AdminTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = maquina.imagen ?: "",
                    color = AdminTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(
                modifier = Modifier.weight(1f),
                label = "Editar",
                icon = Icons.Default.Edit,
                containerColor = AdminCyan,
                contentColor = AdminBgDeep,
                onClick = onEdit
            )

            ActionButton(
                modifier = Modifier.weight(1f),
                label = "Eliminar",
                icon = Icons.Default.DeleteOutline,
                containerColor = AdminDangerSoft,
                contentColor = AdminDanger,
                bordered = true,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun MachineAvatar(
    nombre: String,
    hasImage: Boolean
) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(AdminCyanSoft)
            .border(1.dp, AdminDivider, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (hasImage) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = AdminCyan,
                modifier = Modifier.size(26.dp)
            )
        } else {
            Text(
                text = nombre.firstOrNull()?.uppercase() ?: "M",
                color = AdminCyan,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun MachineIdBadge(id: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AdminBgCardAlt)
            .border(1.dp, AdminDivider, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "ID $id",
            color = AdminTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    bordered: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(containerColor)
            .then(
                if (bordered) {
                    Modifier.border(1.dp, contentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyMachinesCard(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AdminBgCard)
            .border(1.dp, AdminDivider, RoundedCornerShape(24.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(AdminCyanSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = AdminCyan,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "No hay maquinas registradas",
            color = AdminTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Crea tu primera maquina para comenzar a llenar el catalogo del gimnasio.",
            color = AdminTextSecondary,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AdminCyan,
                contentColor = AdminBgDeep
            )
        ) {
            Text("Agregar maquina")
        }
    }
}

@Composable
private fun MachineFormDialog(
    viewModel: MaquinaViewModel,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val form = viewModel.maquinaForm
    val titulo = if (viewModel.isEditing) "Editar maquina" else "Nueva maquina"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminBgCard,
        title = {
            Column {
                Text(
                    text = titulo,
                    color = AdminTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Registra la informacion principal de la maquina",
                    color = AdminTextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = form.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = form.grupo_muscular.orEmpty(),
                    onValueChange = viewModel::onGrupoMuscularChange,
                    label = { Text("Grupo muscular") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = form.descripcion.orEmpty(),
                    onValueChange = viewModel::onDescripcionChange,
                    label = { Text("Descripcion") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = form.imagen.orEmpty(),
                    onValueChange = viewModel::onImagenChange,
                    label = { Text("Imagen (URL o ruta)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth()
                )

                viewModel.formError?.let { error ->
                    Text(
                        text = error,
                        color = AdminDanger,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.guardar {
                        onSaveSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminCyan,
                    contentColor = AdminBgDeep
                )
            ) {
                Text(if (viewModel.isEditing) "Guardar cambios" else "Crear maquina")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = AdminTextSecondary)
            }
        }
    )
}

@Composable
private fun DeleteMachineDialog(
    maquina: MaquinaEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminBgCard,
        title = {
            Text(
                text = "Eliminar maquina",
                color = AdminTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Se eliminara \"${maquina.nombre}\" del catalogo. Esta accion no se puede deshacer.",
                color = AdminTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminDanger,
                    contentColor = Color.White
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = AdminTextSecondary)
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF08111D)
@Composable
private fun AdminMachinesScreenPreview() {
    AdminMachinesScreen()
}
