package uniandes.isis3510.rewereable.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uniandes.isis3510.rewereable.domain.model.User
import uniandes.isis3510.rewereable.ui.components.ActivityOptionCard
import uniandes.isis3510.rewereable.util.AnalyticsHelper

val PrimaryColor = Color(0xFF077288)
val GlassBackground = Color.White.copy(alpha = 0.65f)
val GlassButtonBg = Color.White.copy(alpha = 0.4f)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToPurchases: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsHelper.logScreenView("Profile")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
    ) {
        when (uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ProfileUiState.Error -> {
                val errorMsg = (uiState as ProfileUiState.Error).message
                Text(
                    text = "Oops! $errorMsg",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is ProfileUiState.Success -> {
                val user = (uiState as ProfileUiState.Success).user
                ProfileContent(
                    user = user,
                    onWithdraw = { viewModel.onWithdrawClicked() },
                    onNavigateToPurchases = onNavigateToPurchases,
                    onNavigateToListings = onNavigateToListings,
                    onNavigateToFavorites = onNavigateToFavorites,
                    onLogoutAction = {
                        viewModel.onLogoutClicked()
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    onWithdraw: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onLogoutAction: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 100.dp)
    ) {
        // --- Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { /* Back */ },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(GlassButtonBg)
            ) {
                Icon(Icons.Default.ArrowBack, "Back")
            }

            Text(
                text = "Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Ajustes
                IconButton(
                    onClick = { /* Settings */ },
                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.Settings, "Settings", tint = Color.DarkGray)
                }

                // Botón de Logout
                IconButton(
                    onClick = onLogoutAction,
                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.ExitToApp, "Logout", tint = Color.Red)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Glass Profile Card ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(GlassBackground)
                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "${user.name}'s profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray), // Keeps the gray background while loading or if URL is null
                contentScale = ContentScale.Crop // CRITICAL: This crops the image to fill the circle perfectly without stretching it
            )

                Spacer(modifier = Modifier.height(8.dp))

            Text("${user.name} ${user.lastname}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("${user.location} • Member", color = Color.DarkGray, fontSize = 14.sp)

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = PrimaryColor.copy(alpha = 0.2f))

            Text("CURRENT BALANCE", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text("$${user.balance} COP", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryColor)

            Button(
                onClick = onWithdraw,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = CircleShape,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Withdraw", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("My Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 12.dp))

        // Purchases
        ActivityOptionCard(
            title = "My Purchases",
            subtitle = "${user.purchases.size} active orders", // Dinámico según el modelo
            icon = Icons.Default.ShoppingBag,
            iconColor = PrimaryColor,
            iconBgColor = PrimaryColor.copy(alpha = 0.1f),
            onClick = onNavigateToPurchases
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Listings
        ActivityOptionCard(
            title = "My Listings",
            subtitle = "${user.listings.size} items for sale", // Dinámico según el modelo
            icon = Icons.Default.Storefront,
            iconColor = Color(0xFFEA580C), // Orange
            iconBgColor = Color(0xFFEA580C).copy(alpha = 0.1f),
            onClick = onNavigateToListings
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Favorites
        ActivityOptionCard(
            title = "Favorites",
            subtitle = "${user.favorites.size} saved items", // Dinámico según el modelo
            icon = Icons.Default.Favorite,
            iconColor = Color(0xFFE11D48), // Rose
            iconBgColor = Color(0xFFE11D48).copy(alpha = 0.1f),
            onClick = onNavigateToFavorites
        )
    }
}

