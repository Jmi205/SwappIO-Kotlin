package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.DropOffPoint

interface DropOffRepository{
    suspend fun getNearbyDropOffPoints(lat: Double, lng: Double): Result<List<DropOffPoint>>
    suspend fun getDropOffPointById(id: String): Result<DropOffPoint>
}