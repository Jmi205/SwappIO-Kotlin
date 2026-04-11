package uniandes.isis3510.rewereable.ui.screens.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import uniandes.isis3510.rewereable.ui.components.ProductCard
import uniandes.isis3510.rewereable.util.AnalyticsHelper

@Composable
fun SellerProfileScreen(
    viewModel: SellerProfileViewModel,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsHelper.logScreenView("Seller")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (uiState) {
            is SellerProfileUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is SellerProfileUiState.Error -> Text("Error", modifier = Modifier.align(Alignment.Center), color = Color.Red)
            is SellerProfileUiState.Success -> {
                val state = uiState as SellerProfileUiState.Success
                SellerProfileContent(
                    seller = state.seller,
                    activeProducts = state.activeProducts,
                    isFollowing = state.isFollowing,
                    favoriteIds = state.favoriteIds,
                    onFollowClick = { viewModel.toggleFollow() },
                    onFavoriteClick = {productId -> viewModel.toggleFavorite(productId)},
                    onBackClick = onBackClick,
                    onProductClick = onProductClick
                )
            }
        }
    }
}

@Composable
private fun SellerProfileContent(
    seller: User,
    activeProducts: List<uniandes.isis3510.rewereable.domain.model.Product>,
    isFollowing: Boolean,
    favoriteIds: Set<String>,
    onFollowClick: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val glassModifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White.copy(alpha = 0.5f))
        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f))) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Seller Profile", style = MaterialTheme.typography.titleLarge, fontSize = 20.sp)
            IconButton(onClick = { /* Menu */ }, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f))) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }

        // Hero Section
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar con "badge"
            Box(modifier = Modifier.size(112.dp)) {
                AsyncImage(
                    model = seller.profilePictureUrl,
                    contentDescription = "Seller Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape).border(4.dp, Color.White, CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                // Badge de Verificado
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).size(32.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary).border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("${seller.name} ${seller.lastname}", style = MaterialTheme.typography.titleLarge)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Text("Top Seller", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape).padding(horizontal = 12.dp, vertical = 4.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Text(seller.location, color = Color.Gray, fontSize = 14.sp)
            }

            // Botones de Acción
            Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onFollowClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) Color.Gray else MaterialTheme.colorScheme.primary)
                ) {
                    Icon(if (isFollowing) Icons.Default.Check else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isFollowing) "Following" else "Follow")
                }

                OutlinedButton(
                    onClick = { /* Message */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Message", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        // Stats Grid
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), label = "Sold", value = seller.soldCount.toString())
            StatCard(modifier = Modifier.weight(1f), label = "Active", value = activeProducts.size.toString())
            StatCard(modifier = Modifier.weight(1f), label = "Rating", value = seller.rating.toString(), icon = Icons.Default.Star)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Active Listings", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Grid de Productos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(activeProducts) { product ->
                ProductCard(
                    product = product,
                    isFavorite = favoriteIds.contains(product.id),
                    onFavoriteClick = { onFavoriteClick(product.id)},
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.5f)).border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp).padding(start = 2.dp))
            }
        }
    }
}