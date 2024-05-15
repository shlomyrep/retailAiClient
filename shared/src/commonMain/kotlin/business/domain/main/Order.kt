package business.domain.main

import kotlinx.serialization.SerialName

data class Order(
    var type: Int = Type.NONE.ordinal,
    var customerId: String = "",
    var erpCodeID: String = "",
    @SerialName("email_data")
    var emailData: EmailData = EmailData(),
    var orderStatus: String = "",
)

data class OrderResponse(
    var orderPdf: String = ""
)

data class EmailData(
    var header_image: String = "",
    var products: List<OrderProduct> = listOf(),
    var title: String = "",
    var date: String = "",
    var address: String = "",
)

data class OrderProduct(
    var content: List<Content> = listOf(),
    var main_images: List<String> = listOf(),
    var supplier: String = "",
    var shortDescription: String = "",
    var quantity: Int = 0,
    var price: String = "",
    var roomName: String = "",
    var notes: String = ""
)

data class Content(
    var lines: List<Line> = listOf(),
    var sku: String = ""
)

data class Line(
    var assets: List<String> = listOf(),
    var text: String = "",
    var longDescription: String = ""
)


enum class Type(val type: Int) {
    NONE(0),
    OPEN_BID_IN_ERP(1),
    PDF(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.type == value }
    }
}
