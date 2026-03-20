package uniandes.isis3510.rewereable.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uniandes.isis3510.rewereable.domain.model.Product

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8F8)) // background-light de tu CSS
    ) {
        when (uiState) {
            is DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is DetailUiState.Error -> Text("Error", modifier = Modifier.align(Alignment.Center))
            is DetailUiState.Success -> {
                val product = (uiState as DetailUiState.Success).product
                ProductDetailContent(product, onBackClick)
            }
        }
    }
}

@Composable
private fun ProductDetailContent(product: Product, onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Modificador reutilizable para el efecto "Liquid Glass" de tu CSS
    val glassModifier = Modifier
        .clip(RoundedCornerShape(32.dp))
        .background(Color.White.copy(alpha = 0.65f))
        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp))

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp) // Espacio para la barra de compra
        ) {
            // 1. Imagen Principal (Product Image Hero)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.55f)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            ){
                AsyncImage(
                    model = product.images.getOrNull(0),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(), // Ahora sí llenará el 55% de la pantalla
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.LightGray),
                    error = ColorPainter(Color.LightGray)
                )
            }

            // 2. Tarjeta principal superpuesta (-mt-16)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-48).dp) // Superposición
            ) {
                // Main Details Card
                Column(
                    modifier = glassModifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =  Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Size ${product.size}", color = Color.Gray, fontSize = 12.sp)
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                Text(product.location, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$${product.price}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF077288))
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    Text("Description", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(product.description, color = Color.DarkGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))

                    // Tags
                    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.stateTags.forEach { tag ->
                            Box(modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(tag, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Seller Info Card
                Row(
                    modifier = glassModifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =  Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(product.ownerId, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                Text("4.9 (124 reviews)", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                    IconButton(onClick = { /* Chat */ }) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Chat", tint = Color(0xFF077288))
                    }
                }
            }
        }

        // 3. Top App Bar (Flotante)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement =  Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        // 4. Sticky Bottom Action Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.8f))
                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Price", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("$${product.price}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { /* Buy */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF077288)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Text("Buy Now", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}