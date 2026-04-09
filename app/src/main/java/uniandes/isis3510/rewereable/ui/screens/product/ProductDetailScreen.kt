package uniandes.isis3510.rewereable.ui.screens.product

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import uniandes.isis3510.rewereable.ui.components.ProductCard
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBackClick: () -> Unit,
    onSellerClick: (String) -> Unit,
    onProductClick: (String) -> Unit // ¡NUEVO! Para navegar a las sugerencias
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F8F8))) {
        when (uiState) {
            is DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is DetailUiState.Error -> Text("Error", modifier = Modifier.align(Alignment.Center))
            is DetailUiState.Success -> {
                val data = uiState as DetailUiState.Success
                ProductDetailContent(
                    product = data.product,
                    owner = data.owner,
                    currentUserId = data.currentUserId,
                    suggestions = data.suggestions,
                    onBackClick = onBackClick,
                    onSellerClick = onSellerClick,
                    onProductClick = onProductClick,
                    onDeleteConfirm = { viewModel.deleteProduct(onSuccess = onBackClick) } // Redirige atrás al borrar
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductDetailContent(
    product: Product,
    owner: User,
    currentUserId: String?,
    suggestions: List<Product>,
    onBackClick: () -> Unit,
    onSellerClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onDeleteConfirm: () -> Unit
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // ¡NUEVO! Estado para el Dialog de confirmación
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isOwner = currentUserId == product.ownerId

    // DIALOG DE CONFIRMACIÓN DE BORRADO
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Listing") },
            text = { Text("Are you sure you want to permanently delete this product? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDeleteConfirm() }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    val glassModifier = Modifier
        .clip(RoundedCornerShape(32.dp))
        .background(Color.White.copy(alpha = 0.65f))
        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp))

    val imageCount = if (product.images.isNotEmpty()) product.images.size else 1
    val pagerState = rememberPagerState(pageCount = { imageCount })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 100.dp)
        ) {
            // --- HEADER DE IMÁGENES (Se mantiene igual) ---
            Box(
                modifier = Modifier.fillMaxWidth().height(screenHeight * 0.55f).clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            ) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    AsyncImage(
                        model = product.images.getOrNull(page), contentDescription = null,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(Color.LightGray), error = ColorPainter(Color.LightGray)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-48).dp)
            ) {
                // --- MAIN DETAILS CARD ---
                Column(modifier = glassModifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // ¡NUEVO! Mostramos Size Y Condition
                            Text("Size ${product.size}", color = Color.Gray, fontSize = 12.sp)
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                Text(product.location, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$${product.price}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    Text("Description", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(product.description, color = Color.DarkGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))




                    val conditionContainerColor = when (product.condition) {
                        "New with tags" -> Color(0xFFE8F5E9) // Verde claro
                        "Like New" -> Color(0xFFE3F2FD)      // Azul claro
                        "Good" -> Color(0xFFFFF3E0)          // Naranja claro
                        "Fair" -> Color(0xFFFFEBEE)          // Rojo claro
                        else -> Color(0xFFF5F5F5)            // Gris por defecto
                    }

                    val conditionTextColor = when (product.condition) {
                        "New with tags" -> Color(0xFF2E7D32) // Verde oscuro
                        "Like New" -> Color(0xFF1565C0)      // Azul oscuro
                        "Good" -> Color(0xFFEF6C00)          // Naranja oscuro
                        "Fair" -> Color(0xFFC62828)          // Rojo oscuro
                        else -> Color(0xFF424242)            // Gris oscuro por defecto
                    }

                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(conditionContainerColor)
                                .border(1.dp, conditionTextColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)) // Borde sutil del mismo tono
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = product.condition,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = conditionTextColor
                            )
                        }
                    }






                    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.styleTags.forEach { tag ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(tag, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- MAPA DE UBICACIÓN (NUEVO) ---
                if (product.latitude != 0.0 && product.longitude != 0.0) {
                    val locationLatLng = LatLng(product.latitude!!, product.longitude!!)
                    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(locationLatLng, 14f) }

                    Column(modifier = glassModifier.padding(16.dp).fillMaxWidth()) {
                        Text("Meetup Location", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp))) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = com.google.maps.android.compose.MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false)
                            ) {
                                Marker(state = MarkerState(position = locationLatLng))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- SELLER INFO CARD ---
                Row(
                    modifier = glassModifier.clickable { onSellerClick(owner.id) }.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
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
                    }                }

                // --- SUGGESTIONS CAROUSEL (NUEVO) ---
                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("You Might Also Like", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(suggestions) { suggestedProduct ->
                            Box(modifier = Modifier.width(160.dp)) {
                                ProductCard(
                                    product = suggestedProduct,
                                    isFavorite = false,
                                    onFavoriteClick = { },
                                    onClick = { onProductClick(suggestedProduct.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- TOP BAR (Back Button) ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.5f))) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        // --- STICKY BOTTOM ACTION BAR (ACTUALIZADO) ---
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.9f)).border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp)).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            if (isOwner) {
                // VISTA DEL DUEÑO: Botón de Borrar (Ocupa todo el ancho)
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(48.dp), elevation = null
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Listing", fontWeight = FontWeight.Bold)
                }
            } else {
                // VISTA DE COMPRADOR: Precio y Botón de Comprar (Como estaba antes)
                Column {
                    Text("Total Price", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("$${product.price}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { /* Buy */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}