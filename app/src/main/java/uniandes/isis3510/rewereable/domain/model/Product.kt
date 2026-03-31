package uniandes.isis3510.rewereable.domain.model

import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val discount: Double = 0.0,
    val size: String,
    val brand: String? = null,
    val images: List<String>,
    val location: String,


    val latitude: Double? = null,
    val longitude: Double? = null,

    val description: String,

    val condition: String,
    val stateTags: List<String> = emptyList(),
    val styleTags: List<String> = emptyList(),
    val status: ProductStatus,

    val createdAt: Long = System.currentTimeMillis(), // Auto-asignar la fecha actual
    val updatedAt: Long = System.currentTimeMillis(),

    val ownerId: String
)
