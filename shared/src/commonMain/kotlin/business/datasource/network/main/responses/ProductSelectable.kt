package business.datasource.network.main.responses

import business.domain.main.Category
import business.domain.main.Product
import business.domain.main.Supplier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val TYPE_PRODUCT = "product"


@Serializable
data class ProductSelectable(
    @SerialName("description") val description: String = "",
    @SerialName("_id") override val _id: String = "",
    val id: String = _id,
    @SerialName("image") val image: String? = "",
    @SerialName("isLike") val isLike: Boolean = false,
    @SerialName("name") val name: String = "",
    @SerialName("likes") val likes: Int? = 0,
    @SerialName("price") val price: Int? = 0,
    @SerialName("base_price") override val basePrice: Double = 0.0,
    @SerialName("upgrade_price") override val upgradePrice: Double = 0.0,
    @SerialName("price_type") val priceType: String = "",
    @SerialName("selections") var selections: List<@Contextual Selection> = mutableListOf(),
    @SerialName("rate") val rate: Double? = 0.0,
    @SerialName("title") val title: String = "",
    @SerialName("sku") var sku: String = "",
    @SerialName("category") val category: CategoryDTO = CategoryDTO("", "'", "", null),
    @SerialName("comments") val comments: List<CommentDTO>? = listOf(),
    @SerialName("gallery") val gallery: List<String> = listOf(),
    @SerialName("supplier") val supplier: SupplierDto = SupplierDto(),
    @SerialName("isActive") val isActive: Boolean = true,
    @SerialName("long_description") val longDescription: String = "",
    @SerialName("short_description") val shortDescription: String = "",
    @SerialName("inherits_size") val inheritsSize: Boolean = true,
    @SerialName("is_size_customizable") val isSizeCustomizable: Boolean = true,
    @SerialName("price_include_sub_products") var priceIncludeSubProducts: Boolean = true,
    @SerialName("images") var images: List<Image> = mutableListOf(),
    @SerialName("uuid") var uuid: String = "",
    @SerialName("filter") var filter: Map<String, FilterTags> = mutableMapOf(),
    @SerialName("tags") val tags: List<String> = mutableListOf(),
    @SerialName("final_sale") var finalSale: Int = FinalSale.ZERO.ordinal,
    @SerialName("pdf_url") val pdfUrl: String = "",
    @SerialName("room_name") var roomName: String = ""
) : Selectable {
    companion object {
        const val type = "product"
    }

    fun getCalculatedSku(): String {
        return when (this.priceType) {
            PriceType.SINGLE_PRICE.toString() -> {
                this.sku
            }

            PriceType.SIZES_PRICE.toString() -> {
                (this.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }?.selector?.selected as SizeSelectable).sku
            }

            PriceType.COLOR_PRICE.toString(),
            PriceType.COLOR_SIZES_PRICE.toString() -> {
                // Logic for selecting both color and size
                val colorSelection =
                    this.selections.firstOrNull { it.selector?.selectionType == ColorSelectable.type }

                val sizeSelection =
                    this.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }

                val idSelectedColor = colorSelection?.selector?.selected?._id
                (sizeSelection?.selector?.selected as SizeSelectable).colors[idSelectedColor]?.sku
                    ?: ""
            }

            else -> ""
        }
    }
}

fun ProductSelectable.deepCopy(): ProductSelectable {
    return this.copy(
        selections = this.selections?.map { it.deepCopy() }?.toMutableList() ?: mutableListOf()
    )
}

fun Selection.deepCopy(): Selection {
    return this.copy(
        selectionList = this.selectionList?.map {
            if (it is ProductSelectable) it.deepCopy() else it
        }?.toMutableList() ?: mutableListOf()
    )
}


@Serializable
data class FilterTags(
    val values: List<String>,
    val type: String?
)

@Serializable
data class Image(
    val url: String = "",
    val is_sketch: Boolean = false
)

@Serializable
data class Selection(
    val selector: Selector?,
    @SerialName("selection_list")
    val selectionList: MutableList<Selectable>?
)

@Serializable
data class Selector(
    @SerialName("selected")
    var selected: Selectable?,
    @SerialName("default")
    var default: Selectable?,
    @SerialName("selection_desc")
    val selectionDesc: String?,
    @SerialName("selection_type")
    val selectionType: String?,
    @SerialName("category_id")
    val categoryId: String?
)

@Serializable
sealed interface Selectable {
    val basePrice: Double?
    val upgradePrice: Double?
    val _id: String?
}

@Serializable
data class SizeSelectable(
    @SerialName("base_price")
    override val basePrice: Double? = 0.0,
    @SerialName("upgrade_price")
    override val upgradePrice: Double? = 0.0,
    @SerialName("sku")
    var sku: String = "",
    @SerialName("size")
    val size: String?,
    override val _id: String? = "",
    @SerialName("color_upgrade_pct")
    val colorUpgradePct: Float? = 1F,
    @SerialName("modified")
    var modified: Boolean = false,
    @SerialName("finalSale")
    var finalSale: Int? = FinalSale.ZERO.ordinal,
    @SerialName("colors")
    val colors: Map<String, ColorInfo> = mutableMapOf()
) : Selectable {
    companion object {
        val type = "size"
    }
}

@Serializable
class ColorSelectable(
    @SerialName("base_price")
    override val basePrice: Double? = 0.0,
    @SerialName("upgrade_price")
    override val upgradePrice: Double? = 0.0,
    @SerialName("hex")
    val hex: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("img")
    val img: String?,
    @SerialName("_id")
    override val _id: String?
) : Selectable {
    companion object {
        val type = "color"
    }
}

@Serializable
enum class FinalSale(val type: Int) {
    ZERO(0),
    ONE(1),
    TWO(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.type == value }
    }
}
@Serializable
data class AddImageResult(
    @SerialName("product")
    val product: ProductImageResult,
    @SerialName("message")
    val message: String
)
@Serializable
data class ProductImageResult(
    @SerialName("images")
    val images: List<Image>
)

@Serializable
data class ColorInfo(
    @SerialName("base_price")
    val basePrice: Double? = 0.0,
    @SerialName("upgrade_price")
    val upgradePrice: Double? = 0.0,
    @SerialName("sku")
    val sku: String? = "",
    @SerialName("_id")
    val _id: String? = "",
    @SerialName("not_available")
    val isNotAvailable: Boolean = false
)

@Serializable
enum class PriceType {
    @SerialName("single_price")
    SINGLE_PRICE,

    @SerialName("sizes_price")
    SIZES_PRICE,

    @SerialName("color_sizes_price")
    COLOR_SIZES_PRICE,

    @SerialName("color_price")
    COLOR_PRICE;

    override fun toString(): String {
        return when (this) {
            SINGLE_PRICE -> "single_price"
            SIZES_PRICE -> "sizes_price"
            COLOR_SIZES_PRICE -> "color_sizes_price"
            COLOR_PRICE -> "color_price"
        }
    }

    companion object {

        fun fromString(value: String): PriceType {
            return when (value.lowercase()) {
                "single_price" -> SINGLE_PRICE
                "sizes_price" -> SIZES_PRICE
                "color_sizes_price" -> COLOR_SIZES_PRICE
                "color_price" -> COLOR_PRICE
                else -> SINGLE_PRICE
            }
        }
    }
}

fun ProductSelectable.toProduct() = Product(
    description = description ?: "",
    id = id ?: "0",
    sku = sku ?: "",
    priceType = PriceType.fromString(priceType),
    image = image ?: "",
    isLike = isLike ?: false,
    likes = likes ?: 0,
    price = price ?: 0,
    selections = selections,
    rate = rate ?: 0.0,
    title = title ?: "",
    category = category?.toCategory() ?: Category(),
    comments = comments?.map { it.toComment() } ?: listOf(),
    gallery = gallery ?: listOf(),
    supplier = supplier?.toSupplier() ?: Supplier(),
)

fun getCustomizationSteps(
    product: ProductSelectable,
    selections: MutableList<Selection> = mutableListOf(),
    originalProduct: ProductSelectable
): MutableList<Selection> {
    product.selections?.map { s ->
        var shouldAdd = true
        val originalProductSize =
            originalProduct.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }
        if (product.inheritsSize && s.selector?.selectionType == SizeSelectable.type) {
            // place the right selection in case of inheritance
            originalProductSize?.let {
                it.selector?.selected?.let { sl ->
                    if (!(s.selector?.selected as SizeSelectable).modified) {
                        if (s.selectionList?.contains(sl) == true) {
                            s.selector.selected = sl
                        }
                    }
                }
            }
            if (!product.isSizeCustomizable) {
                shouldAdd = false
            }
        }

        // This section makes sure that whenever someone changes the main product size if there are child selections that are affected and not valid we return to default selection
        if (product.inheritsSize && !product.isSizeCustomizable) {
            product.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }
                ?.let {
                    val sizeStrings =
                        it.selectionList?.map { selectable -> (selectable as SizeSelectable).size }
                    sizeStrings?.let { list ->
                        originalProductSize?.let { originalSizeSelection ->
                            if (!list.contains((originalSizeSelection.selector?.selected as SizeSelectable).size)) {
                                //disabled
                                selections.firstOrNull { sl -> sl.selector?.selected == product }
                                    ?.let { unValidSelection ->
                                        unValidSelection.selector?.default?.let { defSelection ->
                                            unValidSelection.selector.selected = defSelection
                                        }
                                    }
                                // Should we toast here ?
                            }
                        }
                    }
                }
        }

        if (shouldAdd) {
            selections.add(s)
        }
        if (s.selector?.selectionType == ProductSelectable.type && (s.selector.selected as ProductSelectable).isActive) {
            getCustomizationSteps(
                s.selector.selected as ProductSelectable, selections, originalProduct
            )
        }
    }
    return selections
}

