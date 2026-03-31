package uniandes.isis3510.rewereable.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.repository.UserRepository
import uniandes.isis3510.rewereable.domain.model.User


class UserRepositoryImpl : UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()

            if (document.exists()) {

                val purchasesIds = document.get("purchases") as? List<String> ?: emptyList()
                val listingsIds = document.get("listings") as? List<String> ?: emptyList()
                val favoritesIds = document.get("favorites") as? List<String> ?: emptyList()


                val user = User(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    lastname = document.getString("lastname") ?: "",
                    email = document.getString("email") ?: "",
                    number = document.getString("number") ?: "",
                    balance = document.getDouble("balance") ?: 0.0,
                    location = document.getString("location") ?: "Bogotá, CO",
                    memberSince = document.getLong("memberSince") ?: System.currentTimeMillis(),

                    purchases = purchasesIds,
                    listings = listingsIds,
                    favorites = favoritesIds
                )
                Result.success(user)
            } else {
                Result.failure(Exception("El usuario no existe en la base de datos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToFavorites(userId: String, productId: String): Result<Boolean> {
        return try {
            firestore.collection("users").document(userId)
                .update("favorites", FieldValue.arrayUnion(productId))
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(userId: String, productId: String): Result<Boolean> {
        return try {
            firestore.collection("users").document(userId)
                .update("favorites", FieldValue.arrayRemove(productId))
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBalance(userId: String, newBalance: Double): Result<Boolean> {
        return try {
            firestore.collection("users").document(userId)
                .update("balance", newBalance)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}