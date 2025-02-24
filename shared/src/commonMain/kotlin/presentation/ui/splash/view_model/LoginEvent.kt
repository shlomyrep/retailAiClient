package presentation.ui.splash.view_model

import business.core.NetworkState
import business.core.UIComponent
import business.domain.main.SalesMan

sealed class LoginEvent{

    data class OnUpdateNameRegister(val value: String) : LoginEvent()
    data class OnUpdateUsernameLogin(val value: String) : LoginEvent()
    data class OnUpdatePasswordLogin(val value: String) : LoginEvent()
    data class OnSaveSalesManNameManually(val firstName: String, val lastName: String ) : LoginEvent()

    data object Register : LoginEvent()
    data object Login : LoginEvent()
    data object OnRemoveHeadFromQueue : LoginEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : LoginEvent()

    data object OnRetryNetwork : LoginEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ): LoginEvent()

    data class SelectSalesMan(val salesMan: SalesMan) : LoginEvent()
}
