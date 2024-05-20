package business.domain.main

import kotlinx.serialization.Serializable

@Serializable
data class CustomerConfig(
    var customerIdRegex: String = "^(|[45]\\d{7})$",
    var skuRegex: String = "^\\d{9}$",
)
