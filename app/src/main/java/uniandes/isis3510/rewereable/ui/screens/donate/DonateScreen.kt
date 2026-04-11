package uniandes.isis3510.rewereable.ui.screens.donate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uniandes.isis3510.rewereable.domain.model.Charity
import uniandes.isis3510.rewereable.ui.theme.Primary
import uniandes.isis3510.rewereable.util.AnalyticsHelper
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.location.Location
import androidx.compose.runtime.remember

@Composable
fun DonateScreen(
    viewModel: DonateViewModel,
    onNavigateToCharityDetails: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun fetchUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                viewModel.updateUserLocation(location.latitude, location.longitude)
            } else {
                Toast.makeText(context, "Could not get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchUserLocation()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        AnalyticsHelper.logScreenView("Donate")

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            fetchUserLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F1F1))
    ) {
        DonateBackgroundBlobs()

        when (uiState) {
            is DonateUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is DonateUiState.Error -> {
                Text(
                    text = (uiState as DonateUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is DonateUiState.Success -> {
                val state = uiState as DonateUiState.Success
                DonateContent(
                    state = state,
                    onSearch = viewModel::onSearchQueryChanged,
                    onCategorySelected = viewModel::onCategorySelected,
                    onCharityClick = onNavigateToCharityDetails
                )
            }
        }
    }
}

@Composable
private fun DonateContent(
    state: DonateUiState.Success,
    onSearch: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCharityClick: (String) -> Unit
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = topPadding + 6.dp,
                bottom = bottomPadding + 100.dp
            )
    ) {
        DonateHeader()

        Spacer(modifier = Modifier.height(14.dp))

        DonateSearchBar(
            value = state.searchQuery,
            onValueChange = onSearch
        )

        Spacer(modifier = Modifier.height(14.dp))

        CategoryChipsRow(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel("FEATURED CAUSE")

        Spacer(modifier = Modifier.height(8.dp))

        state.featuredCharity?.let { charity ->
            FeaturedCharityCard(
                charity = charity,
                onClick = { onCharityClick(charity.id) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionLabel("ALL CHARITIES")

        Spacer(modifier = Modifier.height(10.dp))

        state.filteredCharities.forEach { charity ->
            CharityCard(
                charity = charity,
                onClick = { onCharityClick(charity.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DonateBackgroundBlobs() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopStart)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFBFE8E3).copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF9DD6E0).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun DonateHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF23313A)
            )
        }

        Text(
            text = "Donate to Charities",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D2B34)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Filter",
                tint = Primary
            )
        }
    }
}

@Composable
private fun DonateSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search charities in Bogotá...",
                color = Color(0xFF94A3AE)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFB9D8DD)
            )
        },
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.78f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.72f),
            focusedTextColor = Color(0xFF1D2B34),
            unfocusedTextColor = Color(0xFF1D2B34)
        )
    )
}

@Composable
private fun CategoryChipsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) Primary else Color.White.copy(alpha = 0.72f))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color(0xFF2D3740),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF7A8993)
    )
}

@Composable
private fun FeaturedCharityCard(
    charity: Charity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FeaturedCardFakeImage()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x66000000),
                                Color(0xC7000000)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFF0E98B0))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "FEATURED",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.White.copy(alpha = 0.88f),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = charity.location,
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = charity.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = charity.description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row {
                        charity.tags.take(2).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.18f))
                                    .padding(horizontal = 9.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }

                    Button(
                        onClick = { onClick() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Primary
                        )
                    ) {
                        Text(
                            text = "Donate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedCardFakeImage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF7A8332),
                        Color(0xFF5D6F2C),
                        Color(0xFF95C2D6),
                        Color(0xFFD0E3EC)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF62764C).copy(alpha = 0.85f))
        )

        Box(
            modifier = Modifier
                .size(width = 58.dp, height = 88.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp, bottom = 18.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(Color(0xFFE1CCB8))
        )

        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 72.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 52.dp, bottom = 18.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(Color(0xFFCFB79D))
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterStart)
                .padding(start = 8.dp, top = 18.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF57691F),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun CharityCard(
    charity: Charity,
    onClick: () -> Unit
) {
    val accentColor = charityAccentColor(charity)
    val icon = charityIcon(charity)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.58f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = charity.name,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = charity.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1D2B34)
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF98A5AF),
                                modifier = Modifier.size(13.dp)
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = charity.location,
                                fontSize = 11.sp,
                                color = Color(0xFF98A5AF)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = charity.description,
                fontSize = 13.sp,
                color = Color(0xFF6A7882),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFE8EEF0))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row {
                    charity.tags.take(2).forEach { tag ->
                        TagBadge(
                            text = tag,
                            accentColor = accentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Text(
                    text = charity.distance,
                    fontSize = 12.sp,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun charityAccentColor(charity: Charity): Color {
    return when {
        charity.tags.any { it.equals("Women", true) } -> Color(0xFFD96BB3)
        charity.tags.any { it.equals("Winter Gear", true) || it.equals("Professional", true) } -> Color(0xFFF6A15B)
        charity.tags.any { it.equals("Children", true) || it.equals("Kids", true) || it.equals("Clothes", true) } -> Color(0xFF2CA3BE)
        else -> Color(0xFF54C68D)
    }
}

private fun charityIcon(charity: Charity): ImageVector {
    return when {
        charity.tags.any { it.equals("Women", true) } -> Icons.Default.Person
        charity.tags.any { it.equals("Children", true) || it.equals("Kids", true) || it.equals("Clothes", true) } -> Icons.Default.Favorite
        else -> Icons.Default.Home
    }
}

@Composable
private fun TagBadge(
    text: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = accentColor,
            fontWeight = FontWeight.Medium
        )
    }
}