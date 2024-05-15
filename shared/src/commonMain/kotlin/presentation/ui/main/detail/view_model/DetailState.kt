package presentation.ui.main.detail.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.core.UIComponentState
import business.datasource.network.main.responses.ProductSelectable
import business.domain.main.HeldInventoryBatch

data class DetailState(
    val product: ProductSelectable = ProductSelectable(),
    val selectedImage: String = "",
    val lastSelection:String = "",
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val productInventoryBatch: HeldInventoryBatch = HeldInventoryBatch(),
    val productInventoryBatchText: String = "",
    val imageOptionDialog: UIComponentState = UIComponentState.Hide,
    val permissionDialog: UIComponentState = UIComponentState.Hide,
    val galleryImages: List<String> = listOf()
)
