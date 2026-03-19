package uniandes.isis3510.rewereable.domain.model

data class Transaction(
    val id: String,
    val productId: String,
    val buyerId: String,
    val sellerId: String,
    val amountPaid: Double,
    val timestamp: Long,
    val status: TransactionStatus

)
