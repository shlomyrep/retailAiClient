package business.datasource.network.main.responses

import business.domain.main.FlashSale
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FlashSaleDTO(
    @SerialName("expired_at") val expiredAt: String? = null,
    @SerialName("products") val products: List<ProductSelectable>? = listOf()
)

fun FlashSaleDTO.toFlashSale() = products?.let {
    FlashSale(
        expiredAt = expiredAt ?: "",
//    products = products?.map { it.toProduct() } ?: listOf(),
        products = it
    )
}
