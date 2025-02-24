package business.datasource.network.main.responses

import business.domain.main.Address
import business.domain.main.CustomerConfig
import business.domain.main.FlashSale
import business.domain.main.Home
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class HomeDTO(
    @SerialName("address") val address: AddressDTO?,
    @SerialName("banners") val banners: List<BannerDTO>?,
    @SerialName("categories") val categories: List<CategoryDTO>?,
    @SerialName("suppliers") val suppliers: List<SupplierDto>?,
    @SerialName("flash_sale") val flashSale: FlashSaleDTO?,
    @SerialName("most_sale") val mostSale: List<ProductSelectable>?,
    @SerialName("newest_product") val newestProduct: List<ProductSelectable>?,
    @SerialName("config") val config: CustomerConfig,
    @SerialName("show_price") val showPrice: Map<String, Boolean> = emptyMap()
)

fun HomeDTO.toHome() = Home(
    address = address?.toAddress() ?: Address(),
    banners = banners?.map { it.toBanner() } ?: listOf(),
    categories = categories?.map { it.toCategory() } ?: listOf(),
    suppliers = suppliers?.map { it.toSupplier() } ?: listOf(),
    flashSale = flashSale?.toFlashSale() ?: FlashSale(),
    mostSale = mostSale?.map { it } ?: listOf(),
    newestProduct = newestProduct?.map { it } ?: listOf(),
    config = config,
    showPrice = showPrice
)
