package uniandes.isis3510.rewereable.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uniandes.isis3510.rewereable.ui.components.GlassTextField
import uniandes.isis3510.rewereable.util.AnalyticsHelper

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsHelper.logScreenView("Register")
    }

    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Escucha el estado para navegar automáticamente cuando el registro sea exitoso
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join Us",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF077288),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Create your ReWearable account",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            GlassTextField(value = name, onValueChange = { name = it }, label = "First Name", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(12.dp))

            GlassTextField(value = lastname, onValueChange = { lastname = it }, label = "Last Name", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(12.dp))

            GlassTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(12.dp))

            GlassTextField(value = password, onValueChange = { password = it }, label = "Password", icon = Icons.Default.Lock, isPassword = true)

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.register(email, password, name, lastname) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF077288)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState !is AuthUiState.Loading && email.isNotBlank() && password.length >= 6
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("Already have an account? ", color = Color.DarkGray)
                Text(
                    text = "Login",
                    color = Color(0xFF077288),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}