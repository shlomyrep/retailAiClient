package business.datasource.network.main.responses

import business.domain.main.BatchItem
import business.domain.main.HeldInventoryBatch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeldInventoryBatchDTO(
    @SerialName("batches_list")
    val batchesList: List<BatchItemDTO> = listOf(),
    @SerialName("held_inventory")
    val heldInventory: Int? = null
)
@Serializable
data class BatchItemDTO(
    @SerialName("batch")
    val batch: String = "",
    @SerialName("quantity")
    val quantity: Double = 0.0,
    @SerialName("free_quantity")
    val freeQuantity: Double = 0.0
)

fun HeldInventoryBatchDTO.toHeldInventoryBatch() = HeldInventoryBatch(
    batchesList = batchesList.map { batchItem ->
        BatchItem(
            batch = batchItem.batch,
            quantity = batchItem.quantity,
            freeQuantity = batchItem.freeQuantity
        )
    },
    heldInventory = heldInventory
)

