package uniandes.isis3510.rewereable.ui.screens.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.ChatChannel
import uniandes.isis3510.rewereable.domain.repository.ChatRepository

sealed class ChatListUiState {
    object Loading : ChatListUiState()
    data class Success(
        val recentChats: List<ChatChannel>,
        val olderChats: List<ChatChannel>,
        val currentUserId: String
    ) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
    object Empty : ChatListUiState()
}

class ChatListViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            _uiState.value = ChatListUiState.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.value = ChatListUiState.Loading

            chatRepository.getUserChats(currentUserId)
                .catch { e ->

                    _uiState.value = ChatListUiState.Error("Error al cargar chats: ${e.message}")
                }
                .collect { chats ->
                    if (chats.isEmpty()) {
                        _uiState.value = ChatListUiState.Empty
                    } else {
                        val twoDaysAgo = System.currentTimeMillis() - (48 * 60 * 60 * 1000)
                        val recentChats = chats.filter { it.lastMessageTimestamp >= twoDaysAgo }
                        val olderChats = chats.filter { it.lastMessageTimestamp < twoDaysAgo }

                        _uiState.value = ChatListUiState.Success(recentChats, olderChats, currentUserId)
                    }
                }
        }
    }

    companion object {
        fun provideFactory(chatRepository: ChatRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatListViewModel(chatRepository) as T
                }
            }
    }
}