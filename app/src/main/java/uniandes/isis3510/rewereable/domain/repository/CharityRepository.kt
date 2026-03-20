package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.Charity

interface CharityRepository {
    suspend fun getCategories(): Result<List<String>>
    suspend fun getCharities(): Result<List<Charity>>
    suspend fun getCharityById(id: String): Result<Charity>
}