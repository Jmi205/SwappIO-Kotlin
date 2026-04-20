package uniandes.isis3510.rewereable.domain.model

data class Checkout(
    val id: String = "",
    val productId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val price: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val paymentMethodLast4: String = "" // Guardamos solo los últimos 4 dígitos por seguridad
)