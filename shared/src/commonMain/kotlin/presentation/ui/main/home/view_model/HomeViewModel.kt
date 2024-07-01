package presentation.ui.main.home.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.ChatMessage
import business.interactors.main.HomeInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.random.Random


class HomeViewModel(
    private val homeInteractor: HomeInteractor
) : ViewModel() {
    val state: MutableState<HomeState> = mutableStateOf(HomeState())

    fun onTriggerEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Error -> appendToMessageQueue(event.uiComponent)
            is HomeEvent.OnRetryNetwork -> {}
            is HomeEvent.OnUpdateNetworkState -> onUpdateNetworkState(event.networkState)
            HomeEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
            is HomeEvent.OnClickSendMessage -> sendMessageToServer(event.message)
        }
    }


    private fun sendMessageToServer(message: String) {
        val newMessage = ChatMessage(
            id = generateRandomId(),
            sender = "User",
            content = message,
            timestamp = getCurrentTimestamp()
        )
        addMessage(newMessage)

        homeInteractor.sendMessage(message).onEach { dataState ->
            when (dataState) {
                is DataState.Data -> {
                    dataState.data?.let {
                        val gptMessage = ChatMessage(
                            id = generateRandomId(),
                            sender = "GPT",
                            content = it.message,
                            timestamp = getCurrentTimestamp()
                        )
                        addMessage(gptMessage) // Add the GPT response to the chat
                    }
                }
                is DataState.Response -> onTriggerEvent(HomeEvent.Error(dataState.uiComponent))
                is DataState.Loading -> state.value = state.value.copy(progressBarState = dataState.progressBarState)
                is DataState.NetworkStatus -> onTriggerEvent(HomeEvent.OnUpdateNetworkState(dataState.networkState))
            }
        }.launchIn(viewModelScope)
    }


    private fun addMessage(message: ChatMessage) {
        val updatedMessages = state.value.chatMessages.toMutableList().apply { add(message) }
        state.value = state.value.copy(chatMessages = updatedMessages)
    }


    private fun appendToMessageQueue(uiComponent: UIComponent) {
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
            println("No messages to remove from the queue")
        }
    }

    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }

    private fun generateRandomId(length: Int = 10): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random(Random.Default) }
            .joinToString("")
    }

    private fun getCurrentTimestamp(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
