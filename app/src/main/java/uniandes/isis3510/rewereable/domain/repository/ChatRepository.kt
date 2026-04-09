package uniandes.isis3510.rewereable.domain.repository

import kotlinx.coroutines.flow.Flow
import uniandes.isis3510.rewereable.domain.model.ChatChannel
import uniandes.isis3510.rewereable.domain.model.Message

interface ChatRepository {
    fun getUserChats(userId: String): Flow<List<ChatChannel>>
    fun getChatMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, senderId: String, text: String): Result<Boolean>
    suspend fun createChatChannel(currentUserId: String,
                                  sellerId: String,
                                  currentUserName: String,
                                  sellerName: String,
                                  currentUserPic: String?,
                                  sellerPic: String?,
                                  productId: String?,
                                  productName: String?,
                                  productPrice: Double?,
                                  productImage: String?  ): Result<String>
    suspend fun getChatChannelById(chatId: String): Result<ChatChannel>
}