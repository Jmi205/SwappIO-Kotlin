package uniandes.isis3510.rewereable.ui.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uniandes.isis3510.rewereable.domain.model.ChatChannel
import uniandes.isis3510.rewereable.ui.theme.GlassBackground
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val searchGlassModifier = Modifier
        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassBackground)
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text("Mensajes", style = MaterialTheme.typography.titleLarge, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Box {
                        IconButton(onClick = { }, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f))) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary)
                        }
                        // Punto rojo de notificación
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Buscar conversaciones...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth().height(52.dp).then(searchGlassModifier),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // --- MAIN CONTENT ---
            when (uiState) {
                is ChatListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                is ChatListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text((uiState as ChatListUiState.Error).message, color = Color.Red)
                    }
                }
                is ChatListUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes mensajes aún.", color = Color.Gray)
                    }
                }
                is ChatListUiState.Success -> {
                    val state = uiState as ChatListUiState.Success

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // RECIENTES
                        if (state.recentChats.isNotEmpty()) {
                            item {
                                Text(
                                    "Recientes",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                )
                            }
                            items(state.recentChats) { chat ->
                                ChatCard(chat, state.currentUserId, onClick = { onChatClick(chat.id) })
                            }
                        }

                        // ANTERIORES
                        if (state.olderChats.isNotEmpty()) {
                            item {
                                Text(
                                    "Anteriores",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 4.dp)
                                )
                            }
                            items(state.olderChats) { chat ->
                                ChatCard(chat, state.currentUserId, isOlder = true, onClick = { onChatClick(chat.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatCard(
    chat: ChatChannel,
    currentUserId: String,
    isOlder: Boolean = false,
    onClick: () -> Unit
) {
    val otherUserId = chat.participantIds.firstOrNull { it != currentUserId } ?: ""
    val otherUserName = chat.participantNames[otherUserId] ?: "Usuario"
    val otherUserProfilePic = chat.participantProfilePics[otherUserId]

    val glassCardModifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White.copy(alpha = if (isOlder) 0.5f else 0.7f))
        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .padding(16.dp)

    Row(
        modifier = Modifier.fillMaxWidth().then(glassCardModifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con indicador de estado (Punto verde o gris)
        Box {
            if (otherUserProfilePic.isNullOrEmpty()) {
                // Initials Avatar
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        otherUserName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            } else {
                AsyncImage(
                    model = otherUserProfilePic,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }

            // Punto de estado
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (isOlder) Color.Gray else Color(0xFF4CAF50))
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Contenido
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = otherUserName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatChatTime(chat.lastMessageTimestamp),
                    fontSize = 12.sp,
                    color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessage.ifBlank { "Enviaste una foto." },
                    fontSize = 14.sp,
                    color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onBackground else Color.DarkGray,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Función inteligente para formatear la fecha
fun formatChatTime(timestamp: Long): String {
    if (timestamp == 0L) return ""

    val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val now = Calendar.getInstance()

    return when {
        // Mismo día
        messageDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(messageDate.time)
        }
        // Día anterior
        messageDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) - 1 -> {
            "Ayer"
        }
        // Misma semana
        now.timeInMillis - timestamp < 7 * 24 * 60 * 60 * 1000L -> {
            SimpleDateFormat("EEEE", Locale("es", "ES")).format(messageDate.time).replaceFirstChar { it.uppercase() }
        }
        // Más antiguo
        else -> {
            SimpleDateFormat("dd MMM", Locale("es", "ES")).format(messageDate.time)
        }
    }
}