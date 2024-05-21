package business.domain.main

import business.datasource.network.main.responses.ProductSelectable

data class Order(
    val products: List<ProductSelectable>,
    val status: Int,
    val code: String,
    var customerId: String,
    val firstName: String,
    val lastName: String,
    val createdAt: String,
    val address: Address,
    val shippingType: ShippingType,
    val orderPdf: String
) {
//    fun getAmount() = "$ ${products.sumOf { it.price } + shippingType.price}"
}
