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
    @SerialName("customer_id") val customerId: String?,
    @SerialName("first_name") val firstName: String?,
    @SerialName("last_name") val lastName: String?,
    @SerialName("shipping_type") val shippingType: Int?,
    @SerialName("status") val status: Int?,
    @SerialName("address") val address: AddressDTO?,
    @SerialName("products") val products: List<ProductSelectable>?,
    @SerialName("pdf") val orderPdf: String?,
    @SerialName("order_id") val orderId: String?

)

fun OrderDTO.toOrder() = Order(
    code = code ?: "",
    createdAt = createdAt ?: "",
    shippingType = shippingType_global[shippingType ?: 0],
    status = status ?: 0,
    customerId = customerId ?: "",
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    address = address?.toAddress() ?: Address(),
    products = products ?: listOf(),
    pdf = orderPdf ?: "",
    orderId = orderId ?: ""
)

