package uniandes.isis3510.rewereable.domain.repository

import kotlinx.coroutines.delay
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.model.ProductStatus

class ProductRepositoryImpl : ProductRepository {

    override suspend fun getCategories(): Result<List<String>> {
        return Result.success(
            listOf("Trending", "Dresses", "Shoes", "Jackets", "Accessories", "Pants")
        )
    }

    override suspend fun getTrendingProducts(): Result<List<Product>> {
        delay(800) // Simulamos tiempo de carga de internet

        return Result.success(
            listOf(
                Product(
                    id = "1",
                    name = "Vintage Floral Summer Dress",
                    description = "Hermoso vestido de verano.",
                    price = 45000.0,
                    size = "M",
                    brand = "Zara",
                    location = "Bogotá",
                    images = listOf("url_vestido"),
                    stateTags = listOf("Casi nuevo"),
                    styleTags = listOf("Floral", "Summer"),
                    status = ProductStatus.AVAILABLE,
                    ownerId = "Ana María" // En un caso real, esto sería un ID que luego busca el nombre
                ),
                Product(
                    id = "2",
                    name = "Classic Leather Biker Jacket",
                    description = "Chaqueta de cuero genuino.",
                    price = 120000.0,
                    size = "L",
                    brand = "Pull & Bear",
                    location = "Medellín",
                    images = listOf("url_chaqueta"),
                    stateTags = listOf("Buen estado"),
                    styleTags = listOf("Leather", "Classic"),
                    status = ProductStatus.AVAILABLE,
                    ownerId = "Carlos R."
                ),
                Product(
                    id = "3",
                    name = "Nike Air Max Red Limited",
                    description = "Tenis edición limitada.",
                    price = 180000.0,
                    size = "42",
                    brand = "Nike",
                    location = "Cali",
                    images = listOf("url_zapatos"),
                    stateTags = listOf("Como nuevo"),
                    styleTags = listOf("Sport", "Sneakers"),
                    status = ProductStatus.AVAILABLE,
                    ownerId = "Valentina"
                ),
                Product(
                    id = "4",
                    name = "Light Wash Denim Jacket",
                    description = "Chaqueta de jean clara.",
                    price = 85000.0,
                    size = "S",
                    brand = "Levi's",
                    location = "Bogotá",
                    images = listOf("url_jean"),
                    stateTags = listOf("Vintage"),
                    styleTags = listOf("Denim", "Casual"),
                    status = ProductStatus.AVAILABLE,
                    ownerId = "Laura B."
                )
            )
        )
    }

    override suspend fun getAvailableProducts(): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun createProductListing(product: Product): Result<Boolean> {
        TODO("Not yet implemented")
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

}