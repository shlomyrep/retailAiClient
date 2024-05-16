package business.datasource.network.main.responses

import business.domain.main.Product
import business.domain.main.SalesMan
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BasketAddRequestDTO(
    @SerialName("count") val count: Int,
    @SerialName("selections") val selections: List<Selection>,
    @SerialName("product_id") val productId: String,
    @SerialName("sales_man") val user: SalesMan,
)
