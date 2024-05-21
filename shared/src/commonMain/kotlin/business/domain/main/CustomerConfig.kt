package business.domain.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerConfig(
    @SerialName("customer_id_regex")
    var customerIdRegex: String = "^(|[45]\\d{7})$",
    @SerialName("sku_regex")
    var skuRegex: String = "^\\d{9}$",
)
