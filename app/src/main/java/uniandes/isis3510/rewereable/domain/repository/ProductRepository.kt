package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.Product

interface ProductRepository {

    suspend fun getAvailableProducts(): Result<List<Product>>
    suspend fun getProductById(productId: String): Result<Product>
    suspend fun createProductListing(product: Product): Result<Boolean>
    suspend fun markProductAsDonated(productId: String, dropOffPointId: String): Result<Boolean>
    suspend fun getProductsByStyle(tag: String): Result<List<Product>>
}