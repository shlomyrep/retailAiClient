package business.datasource.network.main.responses

import business.domain.main.Supplier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SupplierDto(
    @SerialName("company_name") val companyName: String? = "",
    @SerialName("supplier_id") val supplierId: String? = "",
    @SerialName("vat_free") var shouldAddVatToPrice: Boolean? = false,
    @SerialName("image") var image: String? = ""
)

fun SupplierDto.toSupplier() = Supplier(
    companyName = companyName ?: "",
    supplierId = supplierId ?: "",
    shouldAddVatToPrice = shouldAddVatToPrice ?: false,
    image = image ?: ""
)

