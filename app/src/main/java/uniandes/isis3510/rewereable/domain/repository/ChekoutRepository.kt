package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.Checkout

interface CheckoutRepository {
    suspend fun createPurchase(purchase: Checkout): Result<Unit>
}
