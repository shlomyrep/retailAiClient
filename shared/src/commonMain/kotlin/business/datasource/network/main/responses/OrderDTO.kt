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
    shippingType = shippingType_global[shippingType?:0] ,
    status = status ?: 0,
    address = address?.toAddress() ?: Address(),
    products = products?.map { it.toProduct() } ?: listOf(),
)

//data class Order(
//    var type: Int = Type.NONE.ordinal,
//    var customerId: String = "",
//    var erpCodeID: String = "",
//    @SerialName("email_data")
//    var emailData: EmailData = EmailData(),
//    var orderStatus: String = "",
//)
//
//data class OrderResponse(
//    var orderPdf: String = ""
//)
//
//data class EmailData(
//    var header_image: String = "",
//    var products: List<OrderProduct> = listOf(),
//    var title: String = "",
//    var date: String = "",
//    var address: String = "",
//)
//
//data class OrderProduct(
//    var content: List<Content> = listOf(),
//    var main_images: List<String> = listOf(),
//    var supplier: String = "",
//    var shortDescription: String = "",
//    var quantity: Int = 0,
//    var price: String = "",
//    var roomName: String = "",
//    var notes: String = ""
//)
//
//data class Content(
//    var lines: List<Line> = listOf(),
//    var sku: String = ""
//)
//
//data class Line(
//    var assets: List<String> = listOf(),
//    var text: String = "",
//    var longDescription: String = ""
//)
//
//
//enum class Type(val type: Int) {
//    NONE(0),
//    OPEN_BID_IN_ERP(1),
//    PDF(2);
//
//    companion object {
//        fun fromInt(value: Int) = values().first { it.type == value }
//    }
//}
