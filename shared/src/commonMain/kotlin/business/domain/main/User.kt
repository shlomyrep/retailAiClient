package business.domain.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class User(
    val firstName: String = "",
    val image: String = "",
    val lastName: String = ""
) {
    fun fetchName() = "$firstName $lastName"
}

@Serializable
data class SalesMans(
    @SerialName("users")
    val users: List<SalesMan> = listOf()
)

@Serializable
data class SalesMan(
    val username: String = "",
    @SerialName("erp_id")
    val erpID: String = ""
)