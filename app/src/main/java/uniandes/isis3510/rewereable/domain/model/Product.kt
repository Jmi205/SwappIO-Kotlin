package uniandes.isis3510.rewereable.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val discount: Double = 0.0,
    val size: String,
    val images: List<String>,
    val location: String,
    val description: String,
    val stateTags: List<String>,
    val styleTags: List<String>,
    val status: ProductStatus,

    val ownerId: String
)
