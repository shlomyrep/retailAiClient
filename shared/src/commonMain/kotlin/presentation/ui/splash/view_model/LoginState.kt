package presentation.ui.splash.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent

data class LoginState(
    val nameRegister: String = "",
    val usernameLogin: String = "store@tuboul.co.il",
    val passwordLogin: String = "123456",

    val isTokenValid: Boolean = false,
    val navigateToMain: Boolean = false,

    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
)
