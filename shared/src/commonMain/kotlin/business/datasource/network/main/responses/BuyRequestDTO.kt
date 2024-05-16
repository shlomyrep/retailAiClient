package business.datasource.network.main.responses

import business.domain.main.SalesMan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BuyRequestDTO(
    @SerialName("address_id") val addressId: Int = 0,
    @SerialName("shipping_type") val shippingType: Int = 0,
    @SerialName("sales_man") val salesMan: SalesMan,
)
