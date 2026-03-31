package uniandes.isis3510.rewereable.domain.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uniandes.isis3510.rewereable.domain.model.User
import uniandes.isis3510.rewereable.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, pass: String, name: String, lastname: String): Result<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val userId = authResult.user?.uid ?: throw Exception("Error creando usuario")

            val user = User(
                id = userId,
                name = name,
                lastname = lastname,
                email = email,
                balance = 0.0,
                number = "",
                memberSince = System.currentTimeMillis(),
                location = "No especificada",
                profilePictureUrl = null,
                purchases = emptyList(),
                listings = emptyList(),
                favorites = emptyList(),

                rating = 0.0,
                ratingCount = 0,
                soldCount = 0,
                followers = emptyList(),
                following = emptyList()
            )

            firestore.collection("users").document(userId).set(user).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}