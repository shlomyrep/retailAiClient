package business.domain.main

data class HeldInventoryBatch(
    val batchesList: List<BatchItem> = listOf(),
    val heldInventory: Int? = null
)

data class BatchItem(
    val batch: String = "",
    val quantity: Double = 0.0,
    val freeQuantity: Double = 0.0
)