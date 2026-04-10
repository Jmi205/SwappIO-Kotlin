package uniandes.isis3510.rewereable.ui.screens.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
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
import uniandes.isis3510.rewereable.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()

    // Colores basados en tu Tailwind config
    val primaryColor = Color(0xFF077288)
    val tertiaryColor = Color(0xFF74a57f)
    val surfaceColor = Color(0xFFf5f8f8)
    val glassBg = Color.White.copy(alpha = 0.4f)
    val glassBorder = Color.White.copy(alpha = 0.3f)

    Box(modifier = Modifier.fillMaxSize().background(surfaceColor)) {

        // --- LIQUID BACKGROUND EFFECT ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF077288).copy(alpha = 0.05f)) // Fondo base muy suave
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(top = 48.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Gray)
                    }

                    Box {
                        AsyncImage(
                            model = uiState.otherUserPic,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(tertiaryColor)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(uiState.otherUserName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF004e5e))
                        Text("Activo ahora", fontSize = 10.sp, color = tertiaryColor, fontWeight = FontWeight.Medium)
                    }
                }

                IconButton(onClick = { /* Opciones */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
                }
            }

            // --- ITEM CONTEXT CARD (Fijo en la parte superior) ---
            if (uiState.relatedProductId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Padding para separarlo del header
                        .clip(RoundedCornerShape(16.dp))
                        .background(glassBg)
                        .border(1.dp, glassBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = uiState.relatedProductImage,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uiState.relatedProductName ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                        Text("$${uiState.relatedProductPrice ?: 0}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFa37429))
                    }
                    Button(
                        onClick = { onProductClick(uiState.relatedProductId!!) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Ver detalle", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- ÁREA DE MENSAJES (Ocupa el espacio restante) ---
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading && uiState.messages.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true, // Scroll de abajo hacia arriba
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 8.dp)
                    ) {
                        // Mensajes
                        items(uiState.messages) { message ->
                            MessageBubble(
                                message = message,
                                isCurrentUser = message.senderId == uiState.currentUserId,
                                primaryColor = primaryColor
                            )
                        }
                    }
                }
            }
        }

        // --- MESSAGE INPUT (Flotante abajo) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding() // Empuja el input por encima de la barra de navegación de Android
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.85f)) // Fondo un poco más sólido para contraste
                    .border(1.dp, glassBorder, RoundedCornerShape(50))
                    .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Adjuntar imagen */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Gray)
                }

                OutlinedTextField(
                    value = currentMessage,
                    onValueChange = { viewModel.currentMessage.value = it },
                    placeholder = { Text("Escribe un mensaje...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = { viewModel.sendMessage() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(primaryColor),
                    enabled = currentMessage.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean, primaryColor: Color) {
    val bubbleColor = if (isCurrentUser) primaryColor else Color.White.copy(alpha = 0.6f)
    val textColor = if (isCurrentUser) Color.White else Color.DarkGray
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    val shape = if (isCurrentUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(shape)
                    .background(bubbleColor)
                    .border(1.dp, if (isCurrentUser) Color.Transparent else Color.White.copy(alpha = 0.3f), shape)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(text = message.text, color = textColor, fontSize = 14.sp)
            }

            Text(
                text = formatMessageTime(message.timestamp),
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}


fun formatMessageTime(timestamp: Long): String {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}