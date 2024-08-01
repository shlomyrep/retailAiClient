package business.domain.main

import business.datasource.network.main.responses.ProductSelectable


data class Search(
    val products: List<ProductSelectable> = listOf(),
)
