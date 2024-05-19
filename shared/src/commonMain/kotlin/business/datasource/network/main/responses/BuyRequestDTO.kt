package business.datasource.network.main.responses

import business.domain.main.SalesMan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BuyRequestDTO(
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("customer_id") val customerId: String = "",
    @SerialName("sales_man") val salesMan: SalesMan,
    // the map holds a string key for the room name and a list of productId's
    @SerialName("rooms") val rooms: MutableMap<String, List<String>>
)
