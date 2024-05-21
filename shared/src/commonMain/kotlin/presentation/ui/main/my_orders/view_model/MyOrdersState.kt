package presentation.ui.main.my_orders.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.Order
import business.domain.main.Quote

data class MyOrdersState(
    val orders: List<Order> = listOf(),
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val quote: Quote = Quote(),
    val orderPdf: String = "",
    val customerIdRegex: String = "",
)
