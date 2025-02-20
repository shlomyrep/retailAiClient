package presentation.ui.main.suppliers.view_model

import business.core.NetworkState
import business.core.UIComponent

sealed class SuppliersEvent {


   data object OnRemoveHeadFromQueue : SuppliersEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : SuppliersEvent()

   data object OnRetryNetwork : SuppliersEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : SuppliersEvent()

}
