package uniandes.isis3510.rewereable.domain.model

data class ChatChannel(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val participantProfilePics: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0,
    val relatedProductId: String? = null,
    val relatedProductName: String? = null,
    val relatedProductPrice: Double? = null,
    val relatedProductImage: String? = null
)