package uniandes.isis3510.rewereable.ui.screens.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uniandes.isis3510.rewereable.ui.theme.Primary
import uniandes.isis3510.rewereable.ui.theme.GlassBackground

@Composable
fun ProfileScreen() {
    // Usamos Box para el fondo líquido (aquí puedes poner tu gradiente luego)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA)) // Color temporal simulando el fondo líquido
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Back */ }) { Icon(Icons.Default.ArrowBack, "Back") }
                Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { /* Settings */ }) { Icon(Icons.Default.Settings, "Settings") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glass Card Profile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassBackground)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen simulada
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Camila Rodriguez", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Bogotá, CO • Member since 2022", color = Color.Gray, fontSize = 14.sp)

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Primary.copy(alpha = 0.2f))

                Text("CURRENT BALANCE", fontSize = 12.sp, color = Color.Gray)
                Text("$120.000 COP", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Primary)

                Button(
                    onClick = { /* Withdraw */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = CircleShape,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Withdraw")
                }
            }
        }
    }
}