package presentation.ui.splash.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.SalesMan
import business.domain.main.SalesMans

data class LoginState(
    val nameRegister: String = "",
    val usernameLogin: String = "",
    val passwordLogin: String = "",

    val isTokenValid: Boolean = false,
    val navigateToMain: Boolean = false,
    val isSelectedSalesMan: Boolean = false,

    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
    val salesMans: SalesMans? = null,
    val selectedSalesMan: SalesMan? = null,
    val isLoginSucceeded: Boolean = false
)
