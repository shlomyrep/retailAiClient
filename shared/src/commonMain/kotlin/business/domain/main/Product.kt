package business.domain.main

import business.datasource.network.main.responses.Selection
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName

data class Product(
    val description: String = "",
    val id: String = "0",
    val image: String = "",
    val isLike: Boolean = false,
    val likes: Int = 0,
    val selections: List<Selection> = mutableListOf(),
    val price: Int = 0,
    val rate: Double = 0.0,
    val title: String = "",
    val category: Category = Category(),
    val comments: List<Comment> = listOf(),
    val gallery: List<String> = listOf(),
) {
    fun getPrice() = "$ $price"
}

val product_sample = Product(
    description = "Such a great Shoes",
    image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/17c9cb39-4a80-4c27-8ff1-57028b5f91d6/air-force-1-high-womens-shoes-vBhHD4.png",
    isLike = true,
    price = 30,
    rate = 4.4,
    title = "Nike-121",
    category = Category(name = "Shoes")
)