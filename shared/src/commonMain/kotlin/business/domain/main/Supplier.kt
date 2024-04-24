package business.domain.main


data class Supplier(
    val companyName: String = "",
    val supplierId: String = "",
    var shouldAddVatToPrice: Boolean = false
)