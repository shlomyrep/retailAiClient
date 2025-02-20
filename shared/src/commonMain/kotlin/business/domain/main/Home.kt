package business.domain.main

import business.datasource.network.main.responses.ProductSelectable

data class Home(
    val address: Address = Address(),
    val banners: List<Banner> = listOf(),
    val categories: List<Category> = listOf(),
    val suppliers: List<Supplier> = listOf(),
    val flashSale: FlashSale = FlashSale(),
    val mostSale: List<ProductSelectable> = listOf(),
    val newestProduct: List<ProductSelectable> = listOf(),
    val config: CustomerConfig = CustomerConfig()
)