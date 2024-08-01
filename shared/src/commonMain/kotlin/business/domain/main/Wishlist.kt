package business.domain.main

import business.datasource.network.main.responses.ProductSelectable


data class Wishlist(
    val categories: List<Category> = listOf(),
    val products: List<ProductSelectable> = listOf(),
)
