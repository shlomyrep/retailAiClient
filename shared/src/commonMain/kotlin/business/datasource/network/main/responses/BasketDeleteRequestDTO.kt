package business.datasource.network.main.responses

import business.domain.main.SalesMan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BasketDeleteRequestDTO(
    @SerialName("product") val product: String,
    @SerialName("sales_man") val salesMan: SalesMan,
)
