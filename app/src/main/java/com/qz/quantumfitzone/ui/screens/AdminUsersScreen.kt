package com.qz.quantumfitzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.data.model.PersonaEntity
import com.qz.quantumfitzone.viewModel.PersonaEditorState
import com.qz.quantumfitzone.viewModel.PersonaViewModel

private val UsersBgDeep = Color(0xFF07131A)
private val UsersBgCard = Color(0xFF0B1D27)
private val UsersCardBorder = Color(0xFF11394A)
private val UsersCyan = Color(0xFF0BCBFF)
private val UsersCyanSoft = Color(0x220BCBFF)
private val UsersTextPrimary = Color(0xFFEAF8FF)
private val UsersTextSecondary = Color(0xFF7D9CAC)
private val UsersGreen = Color(0xFF1FE08C)
private val UsersGreenSoft = Color(0x221FE08C)
private val UsersRed = Color(0xFFFF6A77)
private val UsersRedSoft = Color(0x22FF6A77)
private val UsersButtonBg = Color(0xFF22343E)

@Composable
fun AdminUsersScreen(
    onNavigateBack: () -> Unit = {},
    onNavigate: (BottomNavDestination) -> Unit = {},
    personaViewModel: PersonaViewModel = viewModel()
) {
    val personas by personaViewModel.listaPersonas.collectAsState(initial = emptyList())
    val filteredUsers = remember(personas, personaViewModel.searchQuery) {
        personaViewModel.filteredPersonas(personas)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UsersBgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp)
        ) {
            UsersTopBar(onNavigateBack = onNavigateBack)
            UsersSearchBar(
                value = personaViewModel.searchQuery,
                onValueChange = personaViewModel::updateSearchQuery
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredUsers.isEmpty()) {
                EmptyUsersState(
                    isSearching = personaViewModel.searchQuery.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredUsers, key = { it.correo }) { persona ->
                        UserCard(
                            persona = persona,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEdit = {
                                personaViewModel.openEditDialog(persona)
                            },
                            onDelete = {
                                personaViewModel.requestDelete(persona)
                            },
                            onToggleStatus = {
                                personaViewModel.toggleStatus(persona)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = personaViewModel::openCreateDialog,
            containerColor = UsersCyan,
            contentColor = UsersBgDeep,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 94.dp)
                .size(62.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAddAlt1,
                contentDescription = "Agregar usuario",
                modifier = Modifier.size(28.dp)
            )
        }

        BottomNavBarAdmin(
            selected = BottomNavDestination.USERS,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        personaViewModel.editorState?.let { currentEditor ->
            PersonaEditorDialog(
                state = currentEditor,
                onDismiss = personaViewModel::dismissEditorDialog,
                onSave = personaViewModel::saveEditor,
                onNombreChange = personaViewModel::updateEditorNombre,
                onCorreoChange = personaViewModel::updateEditorCorreo,
                onPasswordChange = personaViewModel::updateEditorPassword,
                onRolChange = personaViewModel::updateEditorRol,
                onPesoChange = personaViewModel::updateEditorPeso,
                onEstaturaChange = personaViewModel::updateEditorEstatura,
                onEstadoChange = personaViewModel::updateEditorEstado
            )
        }

        personaViewModel.personaToDelete?.let { persona ->
            DeletePersonaDialog(
                persona = persona,
                onDismiss = personaViewModel::dismissDeleteDialog,
                onConfirm = personaViewModel::confirmDelete
            )
        }
    }
}

@Composable
private fun UsersTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = UsersCyan
            )
        }
        Text(
            text = "Manage Users",
            color = UsersTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun UsersSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(UsersBgCard)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = UsersCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = UsersTextPrimary,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                if (value.isBlank()) {
                    Text(
                        text = "Search members by ID or Name...",
                        color = UsersTextSecondary,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun UserCard(
    persona: PersonaEntity,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onToggleStatus: () -> Unit = {}
) {
    val statusColor = if (persona.estado) UsersGreen else UsersRed
    val statusBg = if (persona.estado) UsersGreenSoft else UsersRedSoft
    val statusLabel = if (persona.estado) "Active Member" else "Suspended"

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = UsersBgCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            UsersBgCard,
                            UsersBgCard,
                            UsersCyanSoft
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF091116)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = persona.initials(),
                        color = UsersCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = persona.nombre.ifBlank { "Usuario sin nombre" },
                        color = UsersTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(
                            text = statusLabel,
                            color = statusColor,
                            background = statusBg
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ID: ${persona.correo.take(12)}",
                            color = UsersTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = persona.correo,
                        color = UsersTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opciones",
                    tint = UsersTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = UsersCardBorder.copy(alpha = 0.55f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UserActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Edit",
                    icon = Icons.Default.Edit,
                    onClick = onEdit
                )
                UserActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Delete",
                    icon = Icons.Default.Delete,
                    accentColor = UsersRed,
                    onClick = onDelete
                )
                UserActionButton(
                    modifier = Modifier.weight(1f),
                    text = if (persona.estado) "Suspend" else "Enable",
                    icon = if (persona.estado) Icons.Default.Block else Icons.Default.VerifiedUser,
                    accentColor = statusColor,
                    onClick = onToggleStatus
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    color: Color,
    background: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun UserActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color = UsersTextPrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(UsersButtonBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = accentColor,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun PersonaEditorDialog(
    state: PersonaEditorState,
    onDismiss: () -> Unit,
    onSave: () -> String?,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRolChange: (String) -> Unit,
    onPesoChange: (String) -> Unit,
    onEstaturaChange: (String) -> Unit,
    onEstadoChange: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = UsersBgCard,
        title = {
            Text(
                text = if (state.isEditMode) "Editar usuario" else "Agregar usuario",
                color = UsersTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PersonaTextField(
                    value = state.nombre,
                    onValueChange = onNombreChange,
                    label = "Nombre"
                )
                PersonaTextField(
                    value = state.correo,
                    onValueChange = onCorreoChange,
                    label = "Correo",
                    enabled = !state.isEditMode
                )
                PersonaTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = "Password"
                )
                PersonaTextField(
                    value = state.rol,
                    onValueChange = onRolChange,
                    label = "Rol"
                )
                PersonaTextField(
                    value = state.peso,
                    onValueChange = onPesoChange,
                    label = "Peso"
                )
                PersonaTextField(
                    value = state.estatura,
                    onValueChange = onEstaturaChange,
                    label = "Estatura"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Estado",
                            color = UsersTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (state.estado) "Activo" else "Suspendido",
                            color = UsersTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = state.estado,
                        onCheckedChange = onEstadoChange
                    )
                }
                if (state.isEditMode) {
                    Text(
                        text = "El correo queda fijo porque es la clave principal del usuario.",
                        color = UsersTextSecondary,
                        fontSize = 12.sp
                    )
                }
                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = UsersRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave()
                }
            ) {
                Text(if (state.isEditMode) "Guardar" else "Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PersonaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DeletePersonaDialog(
    persona: PersonaEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = UsersBgCard,
        title = {
            Text(
                text = "Eliminar usuario",
                color = UsersTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Se eliminara a ${persona.nombre.ifBlank { persona.correo }} de forma permanente.",
                color = UsersTextSecondary
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EmptyUsersState(
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(UsersBgCard)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(UsersCyanSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = null,
                tint = UsersCyan,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (isSearching) "No encontramos usuarios" else "Aun no hay usuarios registrados",
            color = UsersTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isSearching) {
                "Prueba con otro nombre o correo."
            } else {
                "Cuando agregues personas a la base de datos apareceran aqui en cards."
            },
            color = UsersTextSecondary,
            fontSize = 13.sp
        )
    }
}

private fun PersonaEntity.initials(): String {
    val cleanName = nombre.trim()
    if (cleanName.isBlank()) return "QF"
    return cleanName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}

@Preview(showBackground = true, backgroundColor = 0xFF07131A)
@Composable
private fun AdminUsersScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UsersBgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp)
        ) {
            UsersTopBar(onNavigateBack = {})
            UsersSearchBar(value = "", onValueChange = {})
            Spacer(modifier = Modifier.height(14.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    listOf(
                        PersonaEntity(nombre = "Neon Runner", correo = "neon.runner@qf.com", estado = true),
                        PersonaEntity(nombre = "Quantum Lifter", correo = "quantum.lifter@qf.com", estado = false),
                        PersonaEntity(nombre = "Cyber Flex", correo = "cyber.flex@qf.com", estado = true)
                    )
                ) { persona ->
                    UserCard(
                        persona = persona,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
        BottomNavBarAdmin(
            selected = BottomNavDestination.USERS,
            onItemClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
