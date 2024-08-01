package business.domain.main

import business.datasource.network.main.responses.ProductSelectable


data class FlashSale(
    val expiredAt: String = "",
    val products: List<ProductSelectable> = listOf()
)