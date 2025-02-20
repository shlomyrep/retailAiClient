package presentation.ui.main.suppliers.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.Supplier

data class SuppliersState(
    val suppliers: List<Supplier> = listOf(),
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
)
