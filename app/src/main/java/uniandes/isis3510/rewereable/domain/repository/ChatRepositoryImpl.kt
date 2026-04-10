package uniandes.isis3510.rewereable.domain.repository


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uniandes.isis3510.rewereable.domain.model.ChatChannel
import uniandes.isis3510.rewereable.domain.model.Message
import java.util.UUID

class ChatRepositoryImpl : ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val chatsCollection = firestore.collection("chats")

    override fun getUserChats(userId: String): Flow<List<ChatChannel>> = callbackFlow {
        val listenerRegistration = chatsCollection
            .whereArrayContains("participantIds", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chats = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ChatChannel(
                            id = doc.id,
                            participantIds = doc.get("participantIds") as? List<String> ?: emptyList(),
                            participantNames = doc.get("participantNames") as? Map<String, String> ?: emptyMap(),
                            participantProfilePics = doc.get("participantProfilePics") as? Map<String, String> ?: emptyMap(),
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTimestamp = doc.getLong("lastMessageTimestamp") ?: 0L,
                            unreadCount = doc.getLong("unreadCount")?.toInt() ?: 0,
                            relatedProductId = doc.getString("relatedProductId"),
                            relatedProductName = doc.getString("relatedProductName"),
                            relatedProductPrice = doc.getDouble("relatedProductPrice"),
                            relatedProductImage = doc.getString("relatedProductImage")
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(chats)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {

        val listenerRegistration = chatsCollection.document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Message(
                            id = doc.id,
                            senderId = doc.getString("senderId") ?: "",
                            text = doc.getString("text") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun sendMessage(chatId: String, senderId: String, text: String): Result<Boolean> {

        return try {

            val batch = firestore.batch()
            val chatRef = chatsCollection.document(chatId)

            val messageId = UUID.randomUUID().toString()
            val messageRef = chatRef.collection("messages").document(messageId)

            val timestamp = System.currentTimeMillis()

            val newMessage = Message(
                id = messageId,
                senderId = senderId,
                text = text,
                timestamp = timestamp,
                isRead = false
            )

            batch.set(messageRef, newMessage)

            batch.update(
                chatRef,
                mapOf(
                    "lastMessage" to text,
                    "lastMessageTimestamp" to timestamp
                )
            )

            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createChatChannel(
        currentUserId: String,
        sellerId: String,
        currentUserName: String,
        sellerName: String,
        currentUserPic: String?,
        sellerPic: String?,
        productId: String?,
        productName: String?,
        productPrice: Double?,
        productImage: String?
    ): Result<String> {
        return try {

            val existingChatsSnapshot = chatsCollection
                .whereArrayContains("participantIds", currentUserId)
                .whereEqualTo("relatedProductId", productId)
                .get()
                .await()

            val existingChat = existingChatsSnapshot.documents.firstOrNull { doc ->
                val participants = doc.get("participantIds") as? List<String> ?: emptyList()
                participants.contains(sellerId)
            }

            if (existingChat != null) {
                return Result.success(existingChat.id)
            }

            val newChatId = UUID.randomUUID().toString()
            val newChatRef = chatsCollection.document(newChatId)

            val participantNames = mapOf(currentUserId to currentUserName, sellerId to sellerName)
            val participantProfilePics = mutableMapOf<String, String>()
            if (!currentUserPic.isNullOrBlank()) participantProfilePics[currentUserId] = currentUserPic
            if (!sellerPic.isNullOrBlank()) participantProfilePics[sellerId] = sellerPic

            val newChatChannel = ChatChannel(
                id = newChatId,
                participantIds = listOf(currentUserId, sellerId),
                participantNames = participantNames,
                participantProfilePics = participantProfilePics,
                lastMessageTimestamp = System.currentTimeMillis(),
                lastMessage = "Chat iniciado",
                relatedProductId = productId,
                relatedProductName = productName,
                relatedProductPrice = productPrice,
                relatedProductImage = productImage
            )

            newChatRef.set(newChatChannel).await()
            Result.success(newChatId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatChannelById(chatId: String): Result<ChatChannel> {
        return try {
            val doc = chatsCollection.document(chatId).get().await()
            if (doc.exists()) {
                val chat = ChatChannel(
                    id = doc.id,
                    participantIds = doc.get("participantIds") as? List<String> ?: emptyList(),
                    participantNames = doc.get("participantNames") as? Map<String, String> ?: emptyMap(),
                    participantProfilePics = doc.get("participantProfilePics") as? Map<String, String> ?: emptyMap(),
                    lastMessage = doc.getString("lastMessage") ?: "",
                    lastMessageTimestamp = doc.getLong("lastMessageTimestamp") ?: 0L,
                    unreadCount = doc.getLong("unreadCount")?.toInt() ?: 0,
                    relatedProductId = doc.getString("relatedProductId"),
                    relatedProductName = doc.getString("relatedProductName"),
                    relatedProductPrice = doc.getDouble("relatedProductPrice"),
                    relatedProductImage = doc.getString("relatedProductImage")
                )
                Result.success(chat)
            } else {
                Result.failure(Exception("Chat no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}