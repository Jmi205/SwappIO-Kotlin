package uniandes.isis3510.rewereable.ui.screens.charity

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uniandes.isis3510.rewereable.domain.model.Charity
import uniandes.isis3510.rewereable.ui.theme.Primary
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import uniandes.isis3510.rewereable.util.AnalyticsHelper

private data class DropOffPreview(
    val title: String,
    val address: String,
    val status: String,
    val distance: String,
    val isOpen: Boolean
)

@Composable
fun CharityDetailScreen(
    viewModel: CharityDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToDropOffMap: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsHelper.logScreenView("CharityDetail")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF1F7F7),
                        Color(0xFFE8F1F1),
                        Color(0xFFE3EFF0)
                    )
                )
            )
    ) {
        DetailBackgroundBlobs()

        when (uiState) {
            is CharityDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is CharityDetailUiState.Error -> {
                Text(
                    text = (uiState as CharityDetailUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is CharityDetailUiState.Success -> {
                CharityDetailContent(
                    charity = (uiState as CharityDetailUiState.Success).charity,
                    onBackClick = onBackClick,
                    onNavigateToDropOffMap = onNavigateToDropOffMap
                )
            }
        }
    }
}

@Composable
private fun CharityDetailContent(
    charity: Charity,
    onBackClick: () -> Unit,
    onNavigateToDropOffMap: () -> Unit
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val previewDropOffs = listOf(
        DropOffPreview(
            title = "Sede Principal - Minuto",
            address = "Calle 81A # 73A - 22",
            status = "Open",
            distance = "0.8 km away",
            isOpen = true
        ),
        DropOffPreview(
            title = "Centro de Acopio Usaquén",
            address = "Carrera 7 # 119 - 14",
            status = "Closes 5pm",
            distance = "4.2 km away",
            isOpen = false
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = topPadding + 4.dp,
                bottom = bottomPadding + 100.dp
            )
    ) {
        CharityDetailHeader(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        CharityHeroSection(charity = charity)

        Spacer(modifier = Modifier.height(18.dp))

        CharityStatsGrid()

        Spacer(modifier = Modifier.height(18.dp))

        ImpactCard(charity = charity)

        Spacer(modifier = Modifier.height(18.dp))

        DropOffPreviewSection(
            dropOffs = previewDropOffs,
            onViewMapClick = onNavigateToDropOffMap
        )

        Spacer(modifier = Modifier.height(18.dp))

        ContactActionsSection(charity = charity)
    }
}

@Composable
private fun DetailBackgroundBlobs() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopStart)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF86C8D5).copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun CharityDetailHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
            .background(Color.White.copy(alpha = 0.62f))
            .padding(horizontal = 6.dp, vertical = 10.dp),
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
            text = "Charity Details",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D2B34)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Share",
                tint = Color(0xFF23313A)
            )
        }
    }
}

@Composable
private fun CharityHeroSection(charity: Charity) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(122.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.90f),
                                Color.White.copy(alpha = 0.45f)
                            )
                        )
                    )
                    .padding(5.dp)
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8DB6C4),
                                    Color(0xFF3A6F82),
                                    Color(0xFF173844)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = charity.name.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = charity.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D2B34),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        VerifiedBadge()

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = charity.impact,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = Color(0xFF6E7C86),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Donate",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Donate Clothes Now",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun VerifiedBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Primary.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "Verified Nonprofit",
            color = Primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CharityStatsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Person,
            number = "12k+",
            label = "Families",
            accent = Color(0xFF4A9BB0)
        )

        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Favorite,
            number = "50k",
            label = "Kg Clothes",
            accent = Color(0xFF54B884)
        )

        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Home,
            number = "450",
            label = "Volunteers",
            accent = Color(0xFFDC9E47)
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    number: String,
    label: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.62f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2B34)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF7A8993),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ImpactCard(charity: Charity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.60f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Our Impact",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2B34)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = charity.description,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFF65757F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Read full report",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Read more",
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DropOffPreviewSection(
    dropOffs: List<DropOffPreview>,
    onViewMapClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Drop-off Points",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2B34)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.60f))
                    .clickable { onViewMapClick() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "View Map",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            dropOffs.forEach { dropOff ->
                DropOffPreviewCard(dropOff = dropOff)
            }
        }
    }
}

@Composable
private fun DropOffPreviewCard(
    dropOff: DropOffPreview
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.62f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(14.dp))
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
                    contentDescription = dropOff.title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dropOff.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D2B34)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = dropOff.address,
                    fontSize = 12.sp,
                    color = Color(0xFF7A8993)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (dropOff.isOpen) Color(0xFFE4F5EA) else Color(0xFFF1F2F4)
                            )
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = dropOff.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (dropOff.isOpen) Color(0xFF2E9B57) else Color(0xFF6F7880)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = dropOff.distance,
                        fontSize = 11.sp,
                        color = Color(0xFF9AA5AD)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Directions",
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ContactActionsSection(
    charity: Charity
) {
    Column {
        Text(
            text = "Contact Info",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D2B34)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.62f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ContactActionButton(
                    icon = Icons.Default.Phone,
                    label = "Call",
                    accent = Color(0xFF2D9DC0)
                )

                ContactActionButton(
                    icon = Icons.Default.Phone,
                    label = "WhatsApp",
                    accent = Color(0xFF32B46C)
                )

                ContactActionButton(
                    icon = Icons.Default.Mail,
                    label = "Email",
                    accent = Color(0xFF9A62E5)
                )

                ContactActionButton(
                    icon = Icons.Default.Language,
                    label = "Website",
                    accent = Color(0xFFE36C9A)
                )
            }

            HorizontalDivider(color = Color(0xFFE7ECEF))

            Column(modifier = Modifier.padding(16.dp)) {
                ContactRow(
                    icon = Icons.Default.Phone,
                    text = charity.number
                )

                Spacer(modifier = Modifier.height(10.dp))

                ContactRow(
                    icon = Icons.Default.Mail,
                    text = charity.email
                )

                Spacer(modifier = Modifier.height(10.dp))

                ContactRow(
                    icon = Icons.Default.Language,
                    text = charity.website
                )
            }
        }
    }
}

@Composable
private fun ContactActionButton(
    icon: ImageVector,
    label: String,
    accent: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF7A8993),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF66757F)
        )
    }
}