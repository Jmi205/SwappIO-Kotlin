package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.User

class UserRepositoryImpl : UserRepository {
    override suspend fun getUserProfile(userId: String): Result<User> {
        // Simulamos un retraso de red de 1 segundo
        kotlinx.coroutines.delay(1000)

        // Devolvemos un usuario de prueba (Hardcoded)
        return Result.success(
            User(
                id = userId,
                name = "Camila",
                lastname = "Rodriguez",
                email = "camila@example.com",
                number = "3001234567",
                balance = 125000.0,
                location = "Bogotá, CO",
                memberSince = System.currentTimeMillis()
            )
        )
    }

    // Por ahora dejamos los demás métodos con una implementación básica
    override suspend fun updateBalance(userId: String, newBalance: Double): Result<Boolean> = Result.success(true)
    override suspend fun addToFavorites(userId: String, productId: String): Result<Boolean> = Result.success(true)
    override suspend fun removeFromFavorites(userId: String, productId: String): Result<Boolean> = Result.success(true)
}