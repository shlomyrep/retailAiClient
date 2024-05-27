package presentation.ui.main.edit_order.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.CUSTOM_TAG
import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.interactors.main.GetEditOrderInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class EditOrderViewModel(
    private val getEditOrderInteractor: GetEditOrderInteractor,
    private val appDataStoreManager: AppDataStore
) : ViewModel() {

    val state: MutableState<EditOrderState> = mutableStateOf(EditOrderState())
    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> get() = _navigateBack

    fun onTriggerEvent(event: EditOrderEvent) {
        when (event) {

            is EditOrderEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }

            is EditOrderEvent.Error -> {
                appendToMessageQueue(event.uiComponent)
            }

            is EditOrderEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is EditOrderEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
    }

    init {
        val orderId = fetchOrderId()
        if (orderId.isNotEmpty()) {
            getOrder(orderId)
            resetOrderId()
        } else {
            _navigateBack.value = true
        }

    }


    private fun getOrder(orderId: String) {
        getEditOrderInteractor.execute(orderId).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(EditOrderEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    onTriggerEvent(EditOrderEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        state.value = state.value.copy(products = it)
                    }
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
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

    private fun onRetryNetwork() {}

    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }

    private fun fetchOrderId(): String {
        var orderId = ""
        viewModelScope.launch {
            orderId = appDataStoreManager.readValue(DataStoreKeys.EDIT_ODER_ID).toString()
        }
        return orderId
    }

    private fun resetOrderId() {
        viewModelScope.launch {
            appDataStoreManager.setValue(
                DataStoreKeys.EDIT_ODER_ID,
                ""
            )
        }
    }

    fun resetNavigateBack() {
        _navigateBack.value = false
    }
}