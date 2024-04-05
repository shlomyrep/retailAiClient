package presentation.ui.main.scanner.view_model

import business.core.NetworkState
import business.core.UIComponent

sealed class ScannerEvent {


   data object OnRemoveHeadFromQueue : ScannerEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : ScannerEvent()

   data object OnRetryNetwork : ScannerEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : ScannerEvent()

}
