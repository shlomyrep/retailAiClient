package business.domain.main

import business.datasource.network.main.responses.ProductSelectable
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val products: List<ProductSelectable>,
    val status: Int,
    val code: String,
    val createdAt: String,
) {
    fun getAmount() = "100$"
}
