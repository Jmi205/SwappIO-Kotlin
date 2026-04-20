package uniandes.isis3510.rewereable.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uniandes.isis3510.rewereable.domain.model.Checkout
import uniandes.isis3510.rewereable.domain.repository.CheckoutRepository
import java.util.UUID

class CheckoutRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CheckoutRepository {

    override suspend fun createPurchase(purchase: Checkout): Result<Unit> {
        return try {
            val buyerId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val newPurchase = purchase.copy(
                id = UUID.randomUUID().toString(),
                buyerId = buyerId
            )

            // Usamos un batch para asegurar que ambas operaciones ocurran juntas
            val batch = firestore.batch()

            // 1. Guardar la compra
            val purchaseRef = firestore.collection("purchases").document(newPurchase.id)
            batch.set(purchaseRef, newPurchase)

            // 2. Marcar el producto como VENDIDO ("SOLD")
            val productRef = firestore.collection("products").document(purchase.productId)
            batch.update(productRef, "status", "SOLD")

            val userRef = firestore.collection("users").document(buyerId)
            batch.update(userRef, "purchases", FieldValue.arrayUnion(purchase.productId))

            // Ejecutar el batch
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}