package uniandes.isis3510.rewereable.domain.model


data class User(
    val id: String,
    val name: String,
    val lastname: String,
    val balance: Double,
    val email: String,
    val number: String,
    val memberSince: Long,
    val location: String,

    val profilePictureUrl: String? = null,


    val purchases: List<String> = emptyList(),
    val listings: List<String> = emptyList(),
    val favorites: List<String> = emptyList()
)
