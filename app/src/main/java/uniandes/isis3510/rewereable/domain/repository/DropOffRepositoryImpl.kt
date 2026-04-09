package uniandes.isis3510.rewereable.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import uniandes.isis3510.rewereable.domain.model.DropOffPoint

class DropOffRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : DropOffRepository {

    private val dropOffPointsCollection = firestore.collection("dropoff_points")

    override suspend fun getNearbyDropOffPoints(lat: Double, lng: Double): Result<List<DropOffPoint>> {
        return try {
            val snapshot = dropOffPointsCollection.get().await()

            val points = snapshot.documents
                .mapNotNull { document -> document.toDropOffPoint() }
                .sortedBy { point -> distanceKm(lat, lng, point.latitude, point.longitude) }

            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDropOffPointById(id: String): Result<DropOffPoint> {
        return try {
            val document = dropOffPointsCollection.document(id).get().await()
            val point = document.toDropOffPoint()

            if (point != null) {
                Result.success(point)
            } else {
                Result.failure(Exception("Drop-off point not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DocumentSnapshot.toDropOffPoint(): DropOffPoint? {
        val name = getString("name") ?: return null
        val address = getString("address") ?: ""
        val description = getString("description") ?: ""
        val country = getString("country") ?: ""
        val city = getString("city") ?: ""
        val latitude = getDouble("latitude") ?: return null
        val longitude = getDouble("longitude") ?: return null
        val opensAt = getString("opensAt") ?: ""
        val closesAt = getString("closesAt") ?: ""

        return DropOffPoint(
            id = id,
            name = name,
            address = address,
            description = description,
            country = country,
            city = city,
            latitude = latitude,
            longitude = longitude,
            opensAt = opensAt,
            closesAt = closesAt
        )
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