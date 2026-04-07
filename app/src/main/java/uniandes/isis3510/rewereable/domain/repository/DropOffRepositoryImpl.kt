package uniandes.isis3510.rewereable.domain.repository

import kotlin.math.*
import uniandes.isis3510.rewereable.domain.model.DropOffPoint

class DropOffRepositoryImpl : DropOffRepository {

    private val points = listOf(
        DropOffPoint(
            id = "drop_1",
            name = "Sede Principal - Minuto",
            address = "Calle 81A # 73A - 22",
            description = "Main donation center with clothing classification and reception.",
            country = "Colombia",
            city = "Bogotá",
            latitude = 4.6906,
            longitude = -74.0759,
            opensAt = "08:00",
            closesAt = "18:00"
        ),
        DropOffPoint(
            id = "drop_2",
            name = "Centro de Acopio Usaquén",
            address = "Carrera 7 # 119 - 14",
            description = "Secondary collection point focused on clothing and winter gear.",
            country = "Colombia",
            city = "Bogotá",
            latitude = 4.7068,
            longitude = -74.0349,
            opensAt = "09:00",
            closesAt = "17:00"
        ),
        DropOffPoint(
            id = "drop_3",
            name = "Punto Solidario Chapinero",
            address = "Calle 63 # 9 - 18",
            description = "Convenient urban drop-off point for quick donations.",
            country = "Colombia",
            city = "Bogotá",
            latitude = 4.6486,
            longitude = -74.0632,
            opensAt = "08:30",
            closesAt = "17:30"
        ),
        DropOffPoint(
            id = "drop_4",
            name = "Centro Comunitario Suba",
            address = "Avenida Suba # 104 - 51",
            description = "Community center receiving uniforms, kids clothes and backpacks.",
            country = "Colombia",
            city = "Bogotá",
            latitude = 4.7412,
            longitude = -74.0846,
            opensAt = "10:00",
            closesAt = "16:00"
        )
    )

    override suspend fun getNearbyDropOffPoints(lat: Double, lng: Double): Result<List<DropOffPoint>> {
        return try {
            val sorted = points.sortedBy { point ->
                distanceKm(lat, lng, point.latitude, point.longitude)
            }
            Result.success(sorted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDropOffPointById(id: String): Result<DropOffPoint> {
        val point = points.find { it.id == id }
        return if (point != null) {
            Result.success(point)
        } else {
            Result.failure(Exception("Drop-off point not found"))
        }
    }

    private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}