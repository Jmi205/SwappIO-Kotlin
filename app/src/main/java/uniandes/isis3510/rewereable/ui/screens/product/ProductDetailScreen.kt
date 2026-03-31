package uniandes.isis3510.rewereable.ui.screens.product

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.model.User

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8F8))
    ) {
        when (uiState) {
            is DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is DetailUiState.Error -> Text("Error xd xd", modifier = Modifier.align(Alignment.Center))
            is DetailUiState.Success -> {
                val data = uiState as DetailUiState.Success
                ProductDetailContent(data.product, data.owner, onBackClick)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductDetailContent(product: Product, owner: User, onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val glassModifier = Modifier
        .clip(RoundedCornerShape(32.dp))
        .background(Color.White.copy(alpha = 0.65f))
        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp))

    val imageCount = if (product.images.isNotEmpty()) product.images.size else 1
    val pagerState = rememberPagerState(pageCount = { imageCount })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.55f)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = product.images.getOrNull(page),
                        contentDescription = "${product.name} image $page",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(Color.LightGray),
                        error = ColorPainter(Color.LightGray)
                    )
                }

                if (product.images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 60.dp), // Subimos un poco para que no quede tapado por la tarjeta
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(product.images.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-48).dp)
            ) {
                // Main Details Card (Mantenido igual)
                Column(modifier = glassModifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
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

                // 3. Seller Info Card (¡ACTUALIZADO CON DATOS REALES!)
                Row(
                    modifier = glassModifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Foto de perfil del dueño
                        AsyncImage(
                            model = owner.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            // Nombre real
                            Text("${owner.name} ${owner.lastname}", fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                Text("Vendedor", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                    IconButton(onClick = { /* Chat */ }) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Chat", tint = Color(0xFF077288))
                    }
                }
            }
        }

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
            ) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
        }

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