package business.datasource.network.main.responses

import business.domain.main.SalesMan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BasketAddRequestDTO(
    @SerialName("selections") val selections: List<Selection>,
    @SerialName("product") val product: ProductSelectable,
    @SerialName("cart_item_id") val cartItemId: String?,
    @SerialName("sales_man") val user: SalesMan,
)
