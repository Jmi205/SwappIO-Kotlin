package uniandes.isis3510.rewereable.ui.screens.add

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import uniandes.isis3510.rewereable.ui.theme.GlassBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val title by viewModel.title.collectAsState()
    val brand by viewModel.brand.collectAsState()
    val price by viewModel.price.collectAsState()
    val size by viewModel.size.collectAsState()
    val condition by viewModel.condition.collectAsState()
    val description by viewModel.description.collectAsState()
    val location by viewModel.location.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()
    val selectedLatLng by viewModel.selectedLatLng.collectAsState()

    val styleTags by viewModel.styleTags.collectAsState()

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.selectedImages.value = uris.map { it.toString() }
        }
    }

    val bogotaCenter = LatLng(4.6097, -74.0817)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogotaCenter, 11f)
    }

    //LaunchedEffect(uiState) {
    //    if (uiState is AddProductUiState.Success) {
    //        Toast.makeText(context, "¡Producto publicado con éxito!", Toast.LENGTH_LONG).show()
    //        onSuccess()
    //    }
    //}

    val glassModifier = Modifier
        .background(GlassBackground, RoundedCornerShape(16.dp))
        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Header ---
            Row(
                modifier = Modifier.fillMaxWidth().background(GlassBackground).padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f))) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("List an Item", style = MaterialTheme.typography.titleLarge, fontSize = 20.sp)
                TextButton(onClick = { /* Help */ }) {
                    Text("Help", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            // --- Formulario (Scrollable) ---
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                if (uiState is AddProductUiState.Error) {
                    Text((uiState as AddProductUiState.Error).message, color = Color.Red, fontWeight = FontWeight.Bold)
                }

                // --- Fotos ---
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Photos", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(140.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.weight(2f).fillMaxHeight().then(glassModifier).clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImages.isNotEmpty()) {
                                AsyncImage(
                                    model = Uri.parse(selectedImages[0]),
                                    contentDescription = "Cover",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Cover Photo", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth().then(glassModifier).clip(RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                                if (selectedImages.size > 1) {
                                    AsyncImage(model = Uri.parse(selectedImages[1]), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                                }
                            }
                            Box(modifier = Modifier.weight(1f).fillMaxWidth().then(glassModifier).clip(RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                                if (selectedImages.size > 2) {
                                    AsyncImage(model = Uri.parse(selectedImages[2]), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // --- Título ---
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.title.value = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Vintage Denim Jacket") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(unfocusedIndicatorColor = Color.LightGray)
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { viewModel.brand.value = it },
                    label = { Text("Brand (Optional)") },
                    placeholder = { Text("e.g. Levi's, Nike, Zara") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                // --- Precio ---
                OutlinedTextField(
                    value = price,
                    onValueChange = { viewModel.price.value = it },
                    label = { Text("Price (COP)") },
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 16.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                // --- Grid: Size & Condition ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    var expandedSize by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = size,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Size") },
                            trailingIcon = {
                                IconButton(onClick = { expandedSize = !expandedSize }) {
                                    Icon(if (expandedSize) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        DropdownMenu(expanded = expandedSize, onDismissRequest = { expandedSize = false }) {
                            listOf("S", "M", "L", "XL", "Unique").forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = { viewModel.size.value = selectionOption; expandedSize = false }
                                )
                            }
                        }
                    }

                    var expandedCond by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = condition,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Condition") },
                            trailingIcon = {
                                IconButton(onClick = { expandedCond = !expandedCond }) {
                                    Icon(if (expandedCond) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        DropdownMenu(expanded = expandedCond, onDismissRequest = { expandedCond = false }) {
                            listOf("New with tags", "Like New", "Good", "Fair").forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = { viewModel.condition.value = selectionOption; expandedCond = false }
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Style Tags (Max 3)", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp))

                    val availableTags = listOf("Denim", "Old Money", "Y2K", "Vintage", "Streetwear", "Minimalist", "Coquette", "Gorpcore")

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableTags) { tag ->
                            val isSelected = styleTags.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleStyleTag(tag) },
                                label = { Text(tag) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // --- Descripción ---
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe the item's condition, brand...") },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 5
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { viewModel.location.value = it },
                        label = { Text("Approximate area") },
                        placeholder = { Text("e.g. Chapinero, Bogotá") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .then(glassModifier)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { newLatLng ->
                                viewModel.selectedLatLng.value = newLatLng
                            }
                        ) {
                            selectedLatLng?.let {
                                Marker(
                                    state = MarkerState(position = it),
                                    title = "Selected location",
                                    snippet = "Tap the map to move the pin"
                                )
                            }
                        }
                    }

                    Text(
                        text = if (selectedLatLng != null)
                            "Exact location selected on map"
                        else
                            "Tap the map to select the exact location",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Botón de Enviar
                Button(
                    onClick = { viewModel.submitProduct() },
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = uiState !is AddProductUiState.Loading
                ) {
                    if (uiState is AddProductUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("List Item", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}