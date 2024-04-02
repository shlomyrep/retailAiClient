package presentation.ui.main.detail

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import business.datasource.network.main.responses.*

object SelectionSerializer : KSerializer<Selection> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Selection")

    override fun serialize(encoder: Encoder, value: Selection) {
        // Implement serialization logic here if needed.
    }

    override fun deserialize(decoder: Decoder): Selection {
        val jsonDecoder =
            decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        val selectorObj = jsonObject["selector"]?.jsonObject
        val selectableType = selectorObj?.get("selection_type")?.jsonPrimitive?.content
        val selectionDesc = selectorObj?.get("selection_desc")?.jsonPrimitive?.contentOrNull
        val catId = selectorObj?.get("category_id")?.jsonPrimitive?.contentOrNull ?: ""
        val selected = selectorObj?.get("selected")

        // The `selectable` could be null if any of the fields above are missing or if the selectableType does not match
        val selectable: Selectable? = selectableType?.let { type ->
            selected?.let { sel ->
                when (type) {
                    SizeSelectable.type -> jsonDecoder.json.decodeFromJsonElement<SizeSelectable>(
                        sel
                    )

                    ColorSelectable.type -> jsonDecoder.json.decodeFromJsonElement<ColorSelectable>(
                        sel
                    )
                    // Assuming ProductSelectable is the correct class instead of ProductDTO
                    ProductDTO.type -> jsonDecoder.json.decodeFromJsonElement<ProductDTO>(sel)
                    else -> null
                }
            }
        }

        val selectionList: List<Selectable>? = selectableType?.let { type ->
            jsonObject["selection_list"]?.jsonArray?.let { jsonArray ->
                when (type) {
                    SizeSelectable.type -> jsonDecoder.json.decodeFromJsonElement<List<SizeSelectable>>(
                        jsonArray
                    )

                    ColorSelectable.type -> jsonDecoder.json.decodeFromJsonElement<List<ColorSelectable>>(
                        jsonArray
                    )
                    // Assuming ProductSelectable is the correct class instead of ProductDTO
                    ProductDTO.type -> jsonDecoder.json.decodeFromJsonElement<List<ProductDTO>>(
                        jsonArray
                    )

                    else -> mutableListOf()
                }
            }
        } ?: mutableListOf()

        // Safely construct the Selector, handling the case where `selectable` might be null
        val selector = if (selectable != null) Selector(
            selectable,
            selectable, // Assuming this is the intended use of `selectable` for both selected and default.
            selectionDesc,
            selectableType ?: "",
            catId
        ) else null

        // Ensure Selection can handle a null Selector if needed or adjust according to your model requirements
        return Selection(selector, selectionList?.toMutableList() ?: mutableListOf())
    }
}
