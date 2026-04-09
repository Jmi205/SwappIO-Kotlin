package uniandes.isis3510.rewereable.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Message
import uniandes.isis3510.rewereable.domain.repository.ChatRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

data class ChatDetailState(
    val isLoading: Boolean = true,
    val messages: List<Message> = emptyList(),
    val otherUserName: String = "Usuario",
    val otherUserPic: String? = null,
    val currentUserId: String = "",
    val error: String? = null,
    val relatedProductId: String? = null,
    val relatedProductName: String? = null,
    val relatedProductPrice: Double? = null,
    val relatedProductImage: String? = null
)

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val chatId: String
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(ChatDetailState())
    val uiState: StateFlow<ChatDetailState> = _uiState.asStateFlow()

    var currentMessage = MutableStateFlow("")

    init {
        loadChatDetails()
    }

    private fun loadChatDetails() {
        val currentUserId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, currentUserId = currentUserId)

            val chatResult = chatRepository.getChatChannelById(chatId)

            if (chatResult.isSuccess) {
                val chatChannel = chatResult.getOrNull()!!

                val otherUserId = chatChannel.participantIds.firstOrNull { it != currentUserId } ?: ""
                Log.d("ChatDetailViewModel", "Related product Id: $chatChannel.relatedProductId")

                _uiState.value = _uiState.value.copy(
                    otherUserName = chatChannel.participantNames[otherUserId] ?: "Usuario",
                    otherUserPic = chatChannel.participantProfilePics[otherUserId],
                    relatedProductId = chatChannel.relatedProductId,
                    relatedProductName = chatChannel.relatedProductName,
                    relatedProductPrice = chatChannel.relatedProductPrice,
                    relatedProductImage = chatChannel.relatedProductImage
                )
            }

            chatRepository.getChatMessages(chatId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false
                )
            }
        }
    }

    fun sendMessage() {
        val text = currentMessage.value.trim()
        val currentUserId = _uiState.value.currentUserId

        if (text.isEmpty() || currentUserId.isEmpty()) return

        currentMessage.value = ""

        viewModelScope.launch {
            Log.e("ChatViewModel", "Llegue 2")

            val result = chatRepository.sendMessage(chatId, currentUserId, text)
            if (result.isSuccess) {
            } else {
                currentMessage.value = text
                _uiState.value = _uiState.value.copy(error = "No se pudo enviar el mensaje")
            }
        }
    }

    companion object {
        fun provideFactory(chatRepository: ChatRepository, chatId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatDetailViewModel(chatRepository, chatId) as T
                }
            }
    }
}