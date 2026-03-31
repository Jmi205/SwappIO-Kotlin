package uniandes.isis3510.rewereable.domain.repository

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<Boolean>
    suspend fun register(email: String, pass: String, name: String, lastname: String): Result<Boolean>
    fun logout()
}