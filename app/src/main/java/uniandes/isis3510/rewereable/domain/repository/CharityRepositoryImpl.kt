package uniandes.isis3510.rewereable.domain.repository

import uniandes.isis3510.rewereable.domain.model.Charity

class CharityRepositoryImpl : CharityRepository {

    private val charities = listOf(
        Charity(
            id = "charity_1",
            name = "Fundación Niños de los Andes",
            location = "Chapinero, Bogotá",
            description = "Dedicated to the protection and rehabilitation of street children, providing shelter and education.",
            tags = listOf("Children", "Clothes"),
            distance = "2.1km away",
            impact = "Protection and rehabilitation of vulnerable children.",
            number = "+57 300 111 2233",
            email = "contacto@ninosandes.org",
            website = "https://ninosandes.org",
            isFeatured = true
        ),
        Charity(
            id = "charity_2",
            name = "Banco de Ropa",
            location = "Usaquén, Bogotá",
            description = "Collecting gently used clothing for families in need across Colombia. We ensure dignity through clean clothes.",
            tags = listOf("Winter Gear", "Men"),
            distance = "1.2km away",
            impact = "Distribution of donated clothing to families in need.",
            number = "+57 300 222 3344",
            email = "info@bancodeRopa.org",
            website = "https://bancodeRopa.org"
        ),
        Charity(
            id = "charity_3",
            name = "Fundación Mujer",
            location = "Teusaquillo, Bogotá",
            description = "Supporting single mothers with professional attire for job interviews and daily essentials.",
            tags = listOf("Women", "Professional"),
            distance = "3.5km away",
            impact = "Support for women through clothing and employability resources.",
            number = "+57 300 333 4455",
            email = "hola@fundacionmujer.org",
            website = "https://fundacionmujer.org"
        ),
        Charity(
            id = "charity_4",
            name = "Escuela Nueva",
            location = "Suba, Bogotá",
            description = "We accept uniforms, shoes, and backpacks for rural students starting their academic year.",
            tags = listOf("Uniforms", "Kids"),
            distance = "5.8km away",
            impact = "School support for children in vulnerable communities.",
            number = "+57 300 444 5566",
            email = "contacto@escuelanueva.org",
            website = "https://escuelanueva.org"
        )
    )

    override suspend fun getCategories(): Result<List<String>> {
        return Result.success(
            listOf("Near Me", "Children", "Winter Gear", "Women", "Books")
        )
    }

    override suspend fun getCharities(): Result<List<Charity>> {
        return Result.success(charities)
    }

    override suspend fun getCharityById(id: String): Result<Charity> {
        val charity = charities.find { it.id == id }
        return if (charity != null) {
            Result.success(charity)
        } else {
            Result.failure(Exception("Charity not found"))
        }
    }
}