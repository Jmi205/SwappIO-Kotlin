package uniandes.isis3510.rewereable.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uniandes.isis3510.rewereable.domain.model.Charity

class CharityRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CharityRepository {

    private val charitiesCollection = firestore.collection("charities")

    override suspend fun getCategories(): Result<List<String>> {
        return try {
            val snapshot = charitiesCollection.get().await()

            val categories = snapshot.documents
                .flatMap { document ->
                    (document.get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                }
                .distinct()
                .sorted()

            Result.success(listOf("Near Me") + categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCharities(): Result<List<Charity>> {
        return try {
            val snapshot = charitiesCollection.get().await()

            val charities = snapshot.documents.mapNotNull { document ->
                document.toCharity()
            }

            Result.success(charities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCharityById(id: String): Result<Charity> {
        return try {
            val document = charitiesCollection.document(id).get().await()
            val charity = document.toCharity()

            if (charity != null) {
                Result.success(charity)
            } else {
                Result.failure(Exception("Charity not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DocumentSnapshot.toCharity(): Charity? {
        val name = getString("name") ?: return null
        val location = getString("location") ?: ""
        val description = getString("description") ?: ""
        val tags = (get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val latitude = getDouble("latitude") ?: return null
        val longitude = getDouble("longitude") ?: return null
        val impact = getString("impact") ?: ""
        val number = getString("number") ?: ""
        val email = getString("email") ?: ""
        val website = getString("website") ?: ""
        val isFeatured = getBoolean("isFeatured") ?: false

        return Charity(
            id = id,
            name = name,
            location = location,
            description = description,
            tags = tags,
            impact = impact,
            number = number,
            email = email,
            website = website,
            isFeatured = isFeatured,
            latitude = latitude,
            longitude = longitude
        )
    }
}