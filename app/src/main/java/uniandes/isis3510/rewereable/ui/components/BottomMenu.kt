package uniandes.isis3510.rewereable.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uniandes.isis3510.rewereable.ui.navigation.Screen
import uniandes.isis3510.rewereable.ui.theme.Primary
import uniandes.isis3510.rewereable.ui.theme.GlassBackground

@Composable
fun BottomMenu(navController: NavController, currentRoute: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(GlassBackground) // Tu efecto Glass
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(Icons.Default.Home, "Home", currentRoute == Screen.Home.route) { navController.navigate(Screen.Home.route) }
            MenuItem(Icons.Default.Favorite, "Donate", currentRoute == Screen.Donate.route) { navController.navigate(Screen.Donate.route) }

            // Botón central "Sell"
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Sell.route) },
                containerColor = Primary,
                shape = CircleShape,
                modifier = Modifier
                    .offset(y = (-12).dp)
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Sell", tint = Color.White)
            }

            MenuItem(Icons.Default.Email, "Inbox", currentRoute == Screen.Inbox.route) { navController.navigate(Screen.Inbox.route) }
            MenuItem(Icons.Default.Person, "Profile", currentRoute == Screen.Profile.route) { navController.navigate(Screen.Profile.route) }
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) Primary else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
            Icon(icon, contentDescription = label, tint = color)
        }
        Text(text = label, fontSize = 10.sp, color = color)
    }
}