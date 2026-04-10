package uniandes.isis3510.rewereable.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import java.util.Calendar
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import uniandes.isis3510.rewereable.domain.model.DropOffPoint
import uniandes.isis3510.rewereable.ui.theme.Primary
import androidx.compose.foundation.layout.height
import uniandes.isis3510.rewereable.util.AnalyticsHelper

private enum class TravelMode {
    WALK, DRIVE
}

@Composable
fun MapDropOffScreen(
    viewModel: MapDropOffViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsHelper.logScreenView("MapDropOff")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8F8))
    ) {
        when (uiState) {
            is MapDropOffUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is MapDropOffUiState.Error -> {
                Text(
                    text = (uiState as MapDropOffUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is MapDropOffUiState.Success -> {
                val state = uiState as MapDropOffUiState.Success
                MapDropOffContent(
                    state = state,
                    onBackClick = onBackClick,
                    onSelectPoint = viewModel::selectDropOff
                )
            }
        }
    }
}

@Composable
private fun MapDropOffContent(
    state: MapDropOffUiState.Success,
    onBackClick: () -> Unit,
    onSelectPoint: (String) -> Unit
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val selectedPoint = state.points.find { it.id == state.selectedPointId } ?: state.points.firstOrNull()
    val closestPoint = state.points.minByOrNull {
        distanceKm(state.userLatitude, state.userLongitude, it.latitude, it.longitude)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(state.userLatitude, state.userLongitude),
            12.4f
        )
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    LaunchedEffect(selectedPoint?.id) {
        selectedPoint?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude),
                13.6f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = mapType),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(state.userLatitude, state.userLongitude)
                ),
                title = "You",
                snippet = "Reference location"
            )

            state.points.forEach { point ->
                Marker(
                    state = rememberUpdatedMarkerState(
                        position = LatLng(point.latitude, point.longitude)
                    ),
                    title = point.name,
                    snippet = point.address,
                    onClick = {
                        onSelectPoint(point.id)
                        false
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.10f))
        )

        MapHeader(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = topPadding + 10.dp
                ),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MapFloatingButton(
                icon = Icons.Default.Layers,
                isPrimary = false,
                onClick = {
                    mapType = if (mapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                }
            )

            MapFloatingButton(
                icon = Icons.Default.MyLocation,
                isPrimary = true,
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(state.userLatitude, state.userLongitude),
                        13.0f
                    )
                }
            )
        }

        closestPoint?.let { point ->
            val distance = distanceKm(
                state.userLatitude,
                state.userLongitude,
                point.latitude,
                point.longitude
            )

            ClosestPointBadge(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 18.dp, bottom = bottomPadding + 232.dp),
                distanceKm = distance
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 14.dp, end = 14.dp, bottom = bottomPadding + 84.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            state.points.forEach { point ->
                val pointDistance = distanceKm(
                    state.userLatitude,
                    state.userLongitude,
                    point.latitude,
                    point.longitude
                )

                DropOffCarouselCard(
                    point = point,
                    isSelected = point.id == selectedPoint?.id,
                    distanceKm = pointDistance,
                    onClick = { onSelectPoint(point.id) }
                )
            }
        }
    }
}

@Composable
private fun MapHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.78f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF23313A)
            )
        }

        Text(
            text = "Nearby Drop-off Points",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D2B34)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filter",
                tint = Color(0xFF23313A)
            )
        }
    }
}

@Composable
private fun MapFloatingButton(
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isPrimary) Primary else Color.White.copy(alpha = 0.78f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Map action",
            tint = if (isPrimary) Color.White else Color(0xFF23313A)
        )
    }
}

@Composable
private fun ClosestPointBadge(
    modifier: Modifier = Modifier,
    distanceKm: Double
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.80f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Primary)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Closest point: ${formatDistanceKm(distanceKm)} km away",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D2B34)
        )
    }
}

@Composable
private fun DropOffCarouselCard(
    point: DropOffPoint,
    isSelected: Boolean,
    distanceKm: Double,
    onClick: () -> Unit
) {
    val openNow = isOpenNow(point.opensAt, point.closesAt)
    val mode = estimatedMode(distanceKm)
    val formattedDistance = formatDistanceKm(distanceKm)
    val estimatedMinutes = estimateMinutes(distanceKm, mode)

    Card(
        modifier = Modifier
            .width(300.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Primary else Color.Transparent,
                shape = RoundedCornerShape(30.dp)
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.80f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF9ED1DB),
                                    Color(0xFF4C8CA0)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = point.name,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = point.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D2B34),
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (openNow) Color(0xFFE6F7EA) else Color(0xFFFFE9E9)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (openNow) "Open" else "Closed",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (openNow) Color(0xFF2E9B57) else Color(0xFFC54242)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = point.address,
                        fontSize = 12.sp,
                        color = Color(0xFF65757F)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (mode == TravelMode.WALK) Icons.Default.DirectionsWalk else Icons.Default.DirectionsCar,
                            contentDescription = "Travel mode",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "$estimatedMinutes min • $formattedDistance km from you",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFE9EEF1))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column {
                        Text(
                            text = "Status",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9AA5AD)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (openNow) "Quiet now" else "Opens later",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4B5A64)
                        )
                    }

                    Column {
                        Text(
                            text = "Hours",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9AA5AD)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (openNow) "Ends at ${point.closesAt}" else "${point.opensAt} - ${point.closesAt}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4B5A64)
                        )
                    }
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (openNow) Primary else Color(0xFFE9EEF1),
                        contentColor = if (openNow) Color.White else Color(0xFF66757F)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Go",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (openNow) "Go" else "Route",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun isOpenNow(opensAt: String, closesAt: String): Boolean {
    val now = currentMinutesOfDay()
    val open = parseMinutes(opensAt)
    val close = parseMinutes(closesAt)
    return now in open..close
}

private fun parseMinutes(value: String): Int {
    val parts = value.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hour * 60 + minute
}

private fun currentMinutesOfDay(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 +
            calendar.get(Calendar.MINUTE)
}

private fun estimatedMode(distanceKm: Double): TravelMode {
    return if (distanceKm <= 1.2) TravelMode.WALK else TravelMode.DRIVE
}

private fun estimateMinutes(distanceKm: Double, mode: TravelMode): Int {
    val minutes = when (mode) {
        TravelMode.WALK -> distanceKm * 12.0
        TravelMode.DRIVE -> {
            when {
                distanceKm <= 3.0 -> distanceKm * 6.0
                distanceKm <= 8.0 -> distanceKm * 4.5
                else -> distanceKm * 4.0
            }
        }
    }
    return minutes.roundToInt().coerceAtLeast(1)
}

private fun formatDistanceKm(distanceKm: Double): String {
    return String.format(Locale.US, "%.1f", distanceKm)
}

private fun distanceKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val earthRadius = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}