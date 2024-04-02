package presentation.ui.main.detail.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.datasource.network.main.responses.SizeSelectable
import business.domain.main.Product

data class DetailState(
    val product: Product = Product(),
    val selectedImage: String = "",
    val sizeSelectable: String = "",
    val colorSelectable: String = "",
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
)
