package uniandes.isis3510.rewereable.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uniandes.isis3510.rewereable.domain.model.Product
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
                )
            )
    ) {
        when (uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is HomeUiState.Error -> {
                Text(
                    text = (uiState as HomeUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is HomeUiState.Success -> {
                val data = uiState as HomeUiState.Success
                HomeContent(
                    state = data,
                    onSearch = viewModel::onSearchQueryChanged,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onProductClick = onNavigateToDetails,
                    onTagSelected = viewModel::onTagSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onSearch: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onTagSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header (TopBar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Menú o atrás */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("SwappIO - Home", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            IconButton(onClick = { /* Notificaciones */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }

        // Search Bar (Glass effect)
        TextField(
            value = state.searchQuery,
            onValueChange = onSearch,
            placeholder = { Text("Search for clothes, brands...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Tune, contentDescription = "Filter") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.4f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.4f),
                disabledContainerColor = Color.White.copy(alpha = 0.4f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories (Horizontal Scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.tag.forEach { category ->
                val isSelected = category == state.selectedTag
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF077288) else Color.White.copy(alpha = 0.3f))
                        .clickable { onTagSelected(category) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Products Now", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(state.filteredProducts) { product ->
                ProductCard(
                    product = product,
                    isFavorite = state.favoriteIds.contains(product.id),
                    onFavoriteClick = { onToggleFavorite(product.id) },
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    // Glass Card
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.4f))
            .clickable { onClick() }
    ) {
        // Área de la imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
        ) {
            AsyncImage(
                model = product.images.getOrNull(0),
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.LightGray),
                error = ColorPainter(Color.LightGray)
            )
            // Precio flotante (Glass Tag)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("$${product.price}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Botón Favorito
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.7f))
                    .clickable { onFavoriteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Fav",
                    modifier = Modifier.size(18.dp),
                    tint = if (isFavorite) Color.Red else Color.Black)
            }
        }

        // Información del producto
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${product.brand ?: "Genérico"} • Talla ${product.size}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 2.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black.copy(alpha = 0.1f))

            // Info del vendedor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = product.ownerId, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}