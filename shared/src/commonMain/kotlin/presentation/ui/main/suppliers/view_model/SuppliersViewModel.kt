package presentation.ui.main.suppliers.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.CUSTOM_TAG
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.interactors.main.HomeInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SuppliersViewModel(
    private val homeInteractor: HomeInteractor,
) : ViewModel() {




    val state: MutableState<SuppliersState> = mutableStateOf(SuppliersState())


    fun onTriggerEvent(event: SuppliersEvent) {
        when (event) {

            is SuppliersEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }

            is SuppliersEvent.Error -> {
                appendToMessageQueue(event.uiComponent)
            }

            is SuppliersEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is SuppliersEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
    }

    init {
        getSuppliers()
    }


    private fun getSuppliers() {
        homeInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(SuppliersEvent.OnUpdateNetworkState(dataState.networkState))
                }
                is DataState.Response -> {
                    onTriggerEvent(SuppliersEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    dataState.data?.let { suppliers ->
                        state.value = state.value.copy(suppliers = suppliers.suppliers.sortedBy{ it.companyName })
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


    private fun onRetryNetwork() {
        getSuppliers()
    }


    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }


}