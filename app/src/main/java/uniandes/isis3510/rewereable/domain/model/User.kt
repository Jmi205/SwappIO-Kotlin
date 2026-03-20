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


    val purchases: List<Product> = emptyList(),
    val listings: List<Product> = emptyList(),
    val favorites: List<Product> = emptyList()
)
