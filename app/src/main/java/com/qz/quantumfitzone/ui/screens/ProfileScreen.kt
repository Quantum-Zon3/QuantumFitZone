package com.qz.quantumfitzone.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.viewModel.PersonaViewModel

private val BgDeep = Color(0xFF080E1A)
private val BgCard = Color(0xFF0D1726)
private val CyanPrimary = Color(0xFF00D4FF)
private val CyanDim = Color(0xFF0A8FAA)
private val CyanGlow = Color(0x3300D4FF)
private val GoldElite = Color(0xFFFFD700)
private val TextPrimary = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DividerColor = Color(0xFF1A2F45)
private val RedSignOut = Color(0xFFFF3B5C)
private val RedDanger = Color(0xFFFF5C7A)

@Composable
fun ProfileScreen(
    context: Context = LocalContext.current,
    onNavigateBack: () -> Unit = {},
    onSignOut: () -> Unit = {},

    onNavigate: (BottomNavDestination) -> Unit = {},
    viewModel: PersonaViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.usuarioActual(context,onSignOut)
    }
    val state = viewModel.personaActual
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val glowAnim = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowAnim.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val accountEditorState = viewModel.accountEditorState

    if (accountEditorState != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissOwnAccountEditor,
            title = {
                Text(
                    text = "Edit My Data",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = accountEditorState.nombre,
                        onValueChange = viewModel::updateOwnNombre,
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountEditorState.correo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountEditorState.peso,
                        onValueChange = viewModel::updateOwnPeso,
                        label = { Text("Weight") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountEditorState.estatura,
                        onValueChange = viewModel::updateOwnEstatura,
                        label = { Text("Height") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountEditorState.password,
                        onValueChange = viewModel::updateOwnPassword,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    accountEditorState.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = RedDanger,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.saveOwnAccount() }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissOwnAccountEditor) {
                    Text("Cancel")
                }
            },
            containerColor = BgCard,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    if (viewModel.showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteCurrentAccount,
            title = {
                Text(
                    text = "Delete account",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta accion eliminara tu cuenta y cerrara tu sesion actual.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCurrentAccount(context) {
                            onSignOut()
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteCurrentAccount) {
                    Text("Cancel")
                }
            },
            containerColor = BgCard,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
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
                .fillMaxWidth()
                .verticalScroll(scrollState)
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
                Box(modifier = Modifier.size(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
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
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, CyanPrimary, CircleShape)
                            .background(BgCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = CyanDim,
                            modifier = Modifier.size(52.dp)
                        )
                    }
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
                    text = state.nombre,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                if (uiState.email.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = uiState.email,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${uiState.role.replaceFirstChar { it.uppercase() }} - Level ${uiState.level}",
                    color = CyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(20.dp))

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
                /*
                StatItem(
                    value = uiState.workouts.toString(),
                    label = "WORKOUTS",
                    valueColor = TextPrimary
                )
                StatDivider()
                StatItem(
                    value = "${uiState.streakDays} Days",
                    label = "STREAK",
                    valueColor = TextPrimary
                )
                StatDivider()
                StatItem(
                    value = "Rank ${uiState.rank}",
                    label = "RANK",
                    valueColor = GoldElite
                )

                 */
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "MY ACCOUNT",
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
                /*
                SettingsRow(
                    icon = Icons.Default.Edit,
                    label = "Edit My Data",
                    onClick = viewModel::openOwnAccountEditor
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.DeleteForever,
                    label = "Delete My Account",
                    badge = "Permanent",
                    badgeColor = RedDanger,
                    onClick = viewModel::requestDeleteCurrentAccount
                )
                 */



            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
                    .clickable {
                        viewModel.signOut(context) {
                            onSignOut()
                        }
                    }
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

            Spacer(Modifier.height(72.dp))
        }

        BottomNavBar(
            selected = BottomNavDestination.PROFILE,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

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
    badgeColor: Color = CyanPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
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
                imageVector = icon,
                contentDescription = label,
                tint = badgeColor,
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
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
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
