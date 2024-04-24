package business.domain.main

import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.PriceType
import business.datasource.network.main.responses.Selection
import business.datasource.network.main.responses.SizeSelectable
import kotlinx.serialization.Contextual

data class Product(
    val description: String = "",
    val id: String = "0",
    val image: String = "",
    val isLike: Boolean = false,
    val likes: Int = 0,
    val selections: List<@Contextual Selection> = mutableListOf(),
    val price: Int = 0,
    val rate: Double = 0.0,
    val title: String = "",
    val category: Category = Category(),
    val comments: List<Comment> = listOf(),
    val gallery: List<String> = listOf(),
    val priceType: PriceType = PriceType.SINGLE_PRICE,
    val sku: String = "",
    val supplier: Supplier = Supplier()
) {
    fun getPrice() = "$ $price"

    fun getCalculatedSku(): String {
        return when (this.priceType) {
            PriceType.SINGLE_PRICE -> {
                this.sku
            }

            PriceType.SIZES_PRICE -> {
                (this.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }?.selector?.selected as SizeSelectable).sku
            }

            PriceType.COLOR_PRICE,
            PriceType.COLOR_SIZES_PRICE -> {
                // Logic for selecting both color and size
                val colorSelection =
                    this.selections.firstOrNull { it.selector?.selectionType == ColorSelectable.type }

                val sizeSelection =
                    this.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }

                val idSelectedColor = colorSelection?.selector?.selected?._id
                (sizeSelection?.selector?.selected as SizeSelectable).colors[idSelectedColor]?.sku
                    ?: ""
            }
        }
    }
}

val product_sample = Product(
    description = "Such a great Shoes",
    image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/17c9cb39-4a80-4c27-8ff1-57028b5f91d6/air-force-1-high-womens-shoes-vBhHD4.png",
    isLike = true,
    price = 30,
    rate = 4.4,
    title = "Nike-121",
    category = Category(name = "Shoes"),
    priceType = PriceType.SINGLE_PRICE,
    sku = "121"
)