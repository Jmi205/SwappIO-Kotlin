package uniandes.isis3510.rewereable.domain.model

data class DropOffPoint(
    val id: String,
    val name: String,
    val address: String,
    val description: String,
    val country: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val opensAt: String,
    val closesAt: String,
)
