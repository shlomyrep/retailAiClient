package presentation.ui.splash.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.CUSTOM_TAG
import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.SalesMan
import business.domain.main.SalesMans
import business.interactors.splash.CheckTokenInteractor
import business.interactors.splash.CheckUserInteractor
import business.interactors.splash.LoginInteractor
import business.interactors.splash.RegisterInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class LoginViewModel(
    private val loginInteractor: LoginInteractor,
    private val registerInteractor: RegisterInteractor,
    private val checkTokenInteractor: CheckTokenInteractor,
    private val checkUserInteractor: CheckUserInteractor,
    private val appDataStoreManager: AppDataStore
) : ViewModel() {


    val state: MutableState<LoginState> = mutableStateOf(LoginState())


    fun onTriggerEvent(event: LoginEvent) {
        when (event) {

            is LoginEvent.Login -> {
                login()
            }

            is LoginEvent.Register -> {
                register()
            }

            is LoginEvent.OnUpdateNameRegister -> {
                onUpdateNameRegister(event.value)
            }

            is LoginEvent.OnUpdatePasswordLogin -> {
                onUpdatePasswordLogin(event.value)
            }

            is LoginEvent.OnUpdateUsernameLogin -> {
                onUpdateUsernameLogin(event.value)
            }

            is LoginEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }

            is LoginEvent.Error -> {
                appendToMessageQueue(event.uiComponent)
            }

            is LoginEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is LoginEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }

            is LoginEvent.SelectSalesMan -> {
                onSalesManSelected(event.salesMan)
            }

            is LoginEvent.OnSaveSalesManNameManually -> {
                onSaveSalesManNameManually(event.firstName, event.lastName)
            }
        }
    }

    init {
        checkToken()
        checkUser()
    }

    private fun checkUser() {
        checkUserInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onTriggerEvent(LoginEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    state.value = state.value.copy(isSelectedSalesMan = dataState.data ?: false)
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)

    }

    private fun checkToken() {
        checkTokenInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onTriggerEvent(LoginEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    state.value = state.value.copy(isTokenValid = dataState.data ?: false)
                    state.value = state.value.copy(navigateToMain = dataState.data ?: false)
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun login() {
        loginInteractor.execute(
            email = state.value.usernameLogin,
            password = state.value.passwordLogin,
        ).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onTriggerEvent(LoginEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    if (dataState.data?.users == null) {
                        state.value = state.value.copy(salesMans = SalesMans())
                    } else {
                        state.value = state.value.copy(salesMans = dataState.data)
                    }
                    state.value = state.value.copy(isLoginSucceeded = true)
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun register() {
        registerInteractor.execute(
            email = state.value.usernameLogin,
            password = state.value.passwordLogin,
            name = state.value.nameRegister
        ).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onTriggerEvent(LoginEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    state.value =
                        state.value.copy(navigateToMain = !dataState.data.isNullOrEmpty())
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun onUpdateNameRegister(value: String) {
        state.value = state.value.copy(nameRegister = value)
    }

    private fun onUpdatePasswordLogin(value: String) {
        state.value = state.value.copy(passwordLogin = value)
    }


    private fun onUpdateUsernameLogin(value: String) {
        state.value = state.value.copy(usernameLogin = value)
    }


    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) {
            println("${CUSTOM_TAG}: onTriggerEvent:  ${uiComponent.message}")
            return
        }

        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove() // can throw exception if empty
            state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("${CUSTOM_TAG}: removeHeadMessage: Nothing to remove from DialogQueue")
        }
    }


    private fun onRetryNetwork() {
    }

    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }


    private fun onSalesManSelected(salesMan: SalesMan) {
        state.value = state.value.copy(selectedSalesMan = salesMan)
        val jsonSalesMan = Json.encodeToString(SalesMan.serializer(), salesMan)
        viewModelScope.launch {
            appDataStoreManager.setValue(
                DataStoreKeys.SALES_MAN,
                jsonSalesMan
            )
        }
    }

    private fun onSaveSalesManNameManually(firstName: String, lastName: String) {
        val salesMan = SalesMan("$firstName $lastName", "-1")
        state.value = state.value.copy(selectedSalesMan = salesMan)
        val jsonSalesMan = Json.encodeToString(SalesMan.serializer(), salesMan)
        viewModelScope.launch {
            appDataStoreManager.setValue(
                DataStoreKeys.SALES_MAN,
                jsonSalesMan
            )
        }
    }
}

