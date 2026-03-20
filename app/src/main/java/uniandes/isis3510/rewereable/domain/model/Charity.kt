package uniandes.isis3510.rewereable.domain.model

data class Charity(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val tags: List<String>,
    val distance: String,
    val impact: String,
    val number: String,
    val email: String,
    val website: String,
    val isFeatured: Boolean = false
)