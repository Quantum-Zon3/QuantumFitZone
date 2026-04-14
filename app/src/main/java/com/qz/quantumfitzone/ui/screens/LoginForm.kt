package com.qz.quantumfitzone.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.viewModel.PersonaViewModel

// ─── Colors ──────────────────────────────────────────────────────────────────

private val BgDeep      = Color(0xFF060D1A)
private val BgMid       = Color(0xFF0A1628)
private val BgCard      = Color(0xFF0D1F35)
private val BgField     = Color(0xFF0A1A2E)
private val BorderField = Color(0xFF1A3A5C)
private val AccentCyan  = Color(0xFF00C8FF)
private val AccentBlue  = Color(0xFF0080FF)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSub     = Color(0xFF8A9BB5)
private val TextHint    = Color(0xFF3A5A7A)

// ─── Login Screen ─────────────────────────────────────────────────────────────

@Composable
fun LoginForm(
    context: Context = LocalContext.current,
    viewModel: PersonaViewModel = viewModel(),
    onBack: () -> Unit = {},
    onLogin: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val state = viewModel.personaEntity
    var passwordVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgMid, BgDeep, BgDeep)
                )
            )
    ) {
        // Glow radial detrás del ícono
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentCyan.copy(alpha = 0.18f),
                                AccentBlue.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height * 0.45f),
                            radius = size.width * 0.6f
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Top bar ──────────────────────────────────────────────────
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
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Logo ícono ───────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF0A2A45), Color(0xFF060D1A))
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(AccentCyan.copy(alpha = 0.7f), AccentBlue.copy(alpha = 0.4f))
                        ),
                        shape = CircleShape
                    )
            ) {
                Text(text = "✦", color = AccentCyan, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Título app ───────────────────────────────────────────────
            Text(
                text = "QUANTUM FIT",
                color = AccentCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Card del formulario ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgCard)
                    .border(
                        width = 1.dp,
                        color = BorderField,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    Text(
                        text = "Welcome Back",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email
                    Text(
                        text = "Email Address",
                        color = TextSub,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LoginTextField(
                        value = state.correo,
                        placeholder = "neon@fitness.com",
                        keyboardType = KeyboardType.Email,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = AccentCyan.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onValueChange = { viewModel.onCorreoChange(it)}
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password label + forgot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            color = TextSub,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Forgot Password?",
                            color = AccentCyan,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onForgotPassword() }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LoginTextField(
                        value = state.password,
                        placeholder = "••••••••",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = AccentCyan.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextSub,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        onValueChange = { viewModel.onPasswordChange(it) }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Botón principal ──────────────────────────────────
                    Button(
                        onClick = {
                            viewModel.login({ onLogin()} ,context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(AccentBlue, AccentCyan)
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                        ) {
                            Text(
                                text = "Access Terminal",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Link registro ────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = TextSub,
                    fontSize = 13.sp
                )
                Text(
                    text = "Initialize Profile",
                    color = AccentCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onRegister() }
                )
            }
        }
    }
}

// ─── Login Text Field ─────────────────────────────────────────────────────────

@Composable
private fun LoginTextField(
    value: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = TextHint, fontSize = 14.sp)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = BgField,
            unfocusedContainerColor = BgField,
            focusedBorderColor = AccentCyan.copy(alpha = 0.8f),
            unfocusedBorderColor = BorderField,
            cursorColor = AccentCyan
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
