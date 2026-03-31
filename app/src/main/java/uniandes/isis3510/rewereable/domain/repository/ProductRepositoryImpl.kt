package uniandes.isis3510.rewereable.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.model.ProductStatus
import uniandes.isis3510.rewereable.domain.repository.ProductRepository

class ProductRepositoryImpl : ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getTags(): Result<List<String>> {
        return Result.success(
            listOf("All", "Trending", "Dresses", "Shoes", "Jackets", "Accessories", "Pants")
        )
    }

    override suspend fun getTrendingProducts(): Result<List<Product>> {
        return try {

            val snapshot = firestore.collection("products")
                .whereEqualTo("status", ProductStatus.AVAILABLE.name)
                .limit(20) // Traemos solo los 20 más recientes para no saturar la red
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { document ->
                mapDocumentToProduct(document)
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableProducts(): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val document = firestore.collection("products").document(productId).get().await()

            if (document.exists()) {
                val product = mapDocumentToProduct(document)
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(Exception("Error al mapear el producto"))
                }
            } else {
                Result.failure(Exception("El producto no existe"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProductListing(product: Product): Result<Boolean> {
        return try {
            firestore.collection("products").document(product.id).set(product).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markProductAsDonated(
        productId: String,
        dropOffPointId: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductsByStyle(tag: String): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    private fun mapDocumentToProduct(document: com.google.firebase.firestore.DocumentSnapshot): Product? {
        return try {
            // Firebase guarda los Enums como Strings, los convertimos de vuelta:
            val statusString = document.getString("status") ?: ProductStatus.AVAILABLE.name
            val statusEnum = try {
                ProductStatus.valueOf(statusString)
            } catch (e: Exception) {
                ProductStatus.AVAILABLE
            }

            Product(
                id = document.id,
                name = document.getString("name") ?: "",
                description = document.getString("description") ?: "",
                price = document.getDouble("price") ?: 0.0,
                discount = document.getDouble("discount") ?: 0.0,
                size = document.getString("size") ?: "U",
                brand = document.getString("brand"),
                location = document.getString("location") ?: "",
                latitude = document.getDouble("latitude")?: 0.0,
                longitude = document.getDouble("longitude")?: 0.0,
                images = document.get("images") as? List<String> ?: emptyList(),
                stateTags = document.get("stateTags") as? List<String> ?: emptyList(),
                styleTags = document.get("styleTags") as? List<String> ?: emptyList(),
                status = statusEnum,
                condition = document.getString("condition") ?: "",
                ownerId = document.getString("ownerId") ?: "",
                createdAt = document.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = document.getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
}