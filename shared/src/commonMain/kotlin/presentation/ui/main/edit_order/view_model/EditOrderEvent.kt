package presentation.ui.main.edit_order.view_model

import business.core.NetworkState
import business.core.UIComponent

sealed class EditOrderEvent {
    data class Error(
        val uiComponent: UIComponent
    ) : EditOrderEvent()

   data object OnRetryNetwork : EditOrderEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : EditOrderEvent()


   data object OnRemoveHeadFromQueue : EditOrderEvent()

}
