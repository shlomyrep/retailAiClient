package business.datasource.network.main.responses

import business.domain.main.Address
import business.domain.main.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import presentation.ui.main.checkout.view_model.shippingType_global


@Serializable
data class OrderDTO(
    @SerialName("code") val code: String?,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("shipping_type") val shippingType: Int?,
    @SerialName("status") val status: Int?,
    @SerialName("address") val address: AddressDTO?,
    @SerialName("products") val products: List<ProductSelectable>?,
)

fun OrderDTO.toOrder() = Order(
    code = code ?: "",
    createdAt = createdAt ?: "",
    status = status ?: 0,
    products = products ?: listOf(),
)

