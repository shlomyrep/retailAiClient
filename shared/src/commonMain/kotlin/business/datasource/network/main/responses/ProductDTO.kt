package business.datasource.network.main.responses

import business.domain.main.Category
import business.domain.main.Product
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val TYPE_PRODUCT = "product"


@Serializable
data class ProductDTO(
    @SerialName("description") val description: String?,
    @SerialName("id") val id: String?,
    @SerialName("image") val image: String?,
    @SerialName("isLike") val isLike: Boolean?,
    @SerialName("likes") val likes: Int?,
    @SerialName("price") val price: Int?,
    @SerialName("selections") val selections: List<@Contextual Selection> = mutableListOf(),
    @SerialName("rate") val rate: Double?,
    @SerialName("title") val title: String?,
    @SerialName("category") val category: CategoryDTO?,
    @SerialName("comments") val comments: List<CommentDTO>?,
    @SerialName("gallery") val gallery: List<String>?,
) : Selectable {
    companion object {
        val type = "product"

    }

    override val basePrice: Double?
        get() = TODO("Not yet implemented")
    override val upgradePrice: Double?
        get() = TODO("Not yet implemented")
    override val _id: String?
        get() = TODO("Not yet implemented")
}
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

fun ProductDTO.toProduct() = Product(
    description = description ?: "",
    id = id ?: "0",
    image = image ?: "",
    isLike = isLike ?: false,
    likes = likes ?: 0,
    price = price ?: 0,
    rate = rate ?: 0.0,
    title = title ?: "",
    category = category?.toCategory() ?: Category(),
    comments = comments?.map { it.toComment() } ?: listOf(),
    gallery = gallery ?: listOf()
)
