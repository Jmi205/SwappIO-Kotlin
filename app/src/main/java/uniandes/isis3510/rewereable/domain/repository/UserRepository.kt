package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.User

interface UserRepository{
    suspend fun getUserProfile(userId: String):Result<User>
    suspend fun updateBalance(userId: String, newBalance: Double): Result<Boolean>
    suspend fun addToFavorites(userId: String, productId: String): Result<Boolean>
    suspend fun removeFromFavorites(userId: String, productId: String): Result<Boolean>

}