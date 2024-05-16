package presentation.ui.main.checkout.view_model

import business.core.NetworkState
import business.core.UIComponent
import business.core.UIComponentState
import business.domain.main.Address
import business.domain.main.ShippingType

sealed class CheckoutEvent {

    data class OnUpdateSelectedShipping(val value: ShippingType) : CheckoutEvent()

    data class OnUpdateSelectShippingDialogState(val value: UIComponentState) : CheckoutEvent()

    data class OnUpdateSelectedAddress(val value: Address) : CheckoutEvent()
    data class OnUpdateFirstName(val value: String) : CheckoutEvent()
    data class OnUpdateLastName(val value: String) : CheckoutEvent()
    data class OnUpdateCustomerID(val value: String) : CheckoutEvent()

    data object BuyProduct : CheckoutEvent()

    data object OnRemoveHeadFromQueue : CheckoutEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : CheckoutEvent()

    data object OnRetryNetwork : CheckoutEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : CheckoutEvent()
}
