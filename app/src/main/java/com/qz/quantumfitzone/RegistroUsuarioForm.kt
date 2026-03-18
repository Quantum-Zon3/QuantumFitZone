package com.qz.quantumfitzone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qz.quantumfitzone.clases.RegistroUsuario
import com.qz.quantumfitzone.ui.theme.QuantumFitZoneTheme
import com.qz.quantumfitzone.viewModel.RegistroViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

// ─── Colors ──────────────────────────────────────────────────────────────────

private val BgDeep        = Color(0xFF0A0B1A)
private val BgCard        = Color(0xFF12142A)
private val BgField       = Color(0xFF1A1D35)
private val BorderField   = Color(0xFF2A2D4A)
private val AccentPink    = Color(0xFFE91E8C)
private val AccentPinkEnd = Color(0xFFFF4FC8)
private val AccentCyan    = Color(0xFF00E5FF)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextHint      = Color(0xFF6B6F8E)
private val TextLabel     = Color(0xFFB0B3CC)


@Composable
fun RegistroUsuarioForm(
    viewModel: RegistroViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val state = viewModel.registro

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
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
                Text(
                    text = "Create Account",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Logo upload ────────────────────────────────────────────
            logo()

            Spacer(modifier = Modifier.height(36.dp))

            // ── Campos ───────────────────────────────────────────────────
            QzTextField(
                label = "Full Name",
                value = state.nombre,
                placeholder = "John Doe",
                onValueChange = viewModel::onNombreChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            QzTextField(
                label = "Email",
                value = state.correo,
                placeholder = "john@quantumfit.com",
                keyboardType = KeyboardType.Email,
                onValueChange = viewModel::onCorreoChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            QzPasswordField(
                label = "Password",
                value = state.password,
                onValueChange = viewModel::onPasswordChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            QzTextField(
                label = "Peso (kg)",
                value = if (state.peso.isNaN()) "" else state.peso.toString(),
                placeholder = "70.0",
                keyboardType = KeyboardType.Decimal,
                onValueChange = { viewModel.onPesochange(it.toFloatOrNull() ?: Float.NaN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            QzTextField(
                label = "Estatura (m)",
                value = if (state.estatura.isNaN()) "" else state.estatura.toString(),
                placeholder = "1.75",
                keyboardType = KeyboardType.Decimal,
                onValueChange = { viewModel.onEstaturachange(it.toFloatOrNull() ?: Float.NaN) }
            )


            Spacer(modifier = Modifier.height(32.dp))

            // ── Botones ──────────────────────────────────────────────────
            CreateAccountButton(onClick = viewModel::registrar)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Resultado IMC ────────────────────────────────────────────
            if (state.resultado.isNotEmpty()) {
                ResultadoCard(resultado = state.resultado)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Login link ───────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    color = TextHint,
                    fontSize = 13.sp
                )
                Text(
                    text = "Log In",
                    color = AccentPink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Avatar Upload ────────────────────────────────────────────────────────────

@Composable
private fun logo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(BgField)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(AccentPink, AccentCyan)
                    ),
                    shape = CircleShape
                )
                .clickable { }
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ─── Text Field ───────────────────────────────────────────────────────────────

@Composable
fun QzTextField(
    label: String,
    value: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = TextHint, fontSize = 14.sp)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = BgField,
                unfocusedContainerColor = BgField,
                focusedBorderColor = AccentPink,
                unfocusedBorderColor = BorderField,
                cursorColor = AccentPink
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─── Password Field ───────────────────────────────────────────────────────────

@Composable
fun QzPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = "••••••••", color = TextHint, fontSize = 14.sp)
            },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = TextHint
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = BgField,
                unfocusedContainerColor = BgField,
                focusedBorderColor = AccentPink,
                unfocusedBorderColor = BorderField,
                cursorColor = AccentPink
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─── Fitness Goal Dropdown ────────────────────────────────────────────────────

@Composable
private fun FitnessGoalDropdown() {
    val goals = listOf("Lose Weight", "Build Muscle", "Improve Endurance", "Stay Active")
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Fitness Goal",
            color = TextLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BgField)
                .border(1.dp, BorderField, RoundedCornerShape(14.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selected.isEmpty()) "Select a goal" else selected,
                    color = if (selected.isEmpty()) TextHint else TextPrimary,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextHint
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(BgCard)
                .border(1.dp, BorderField, RoundedCornerShape(8.dp))
        ) {
            goals.forEach { goal ->
                DropdownMenuItem(
                    text = { Text(goal, color = TextPrimary, fontSize = 14.sp) },
                    onClick = {
                        selected = goal
                        expanded = false
                    }
                )
            }
        }
    }
}

// ─── Create Account Button ────────────────────────────────────────────────────

@Composable
private fun CreateAccountButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(AccentPink, AccentPinkEnd)
                    ),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Text(
                text = "Create Account",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Resultado Card ───────────────────────────────────────────────────────────

@Composable
private fun ResultadoCard(resultado: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgField)
            .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = resultado,
            color = AccentCyan,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
