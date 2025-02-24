package presentation.ui.main.my_orders.view_model

import business.core.NetworkState
import business.core.UIComponent
import business.domain.main.Order

sealed class MyOrdersEvent {

    data object OnRemoveHeadFromQueue : MyOrdersEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : MyOrdersEvent()

    data object OnRetryNetwork : MyOrdersEvent()
    data class OnSendQuote(
        val orderType: Int,
        val order: Order,
        val shouldSplitPdf: Boolean
    ) : MyOrdersEvent()

    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : MyOrdersEvent()

    data class OnEditOrder(
        val orderId: String
    ) : MyOrdersEvent()
}
