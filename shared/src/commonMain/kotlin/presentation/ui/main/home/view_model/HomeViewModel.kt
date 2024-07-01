import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.ChatMessage
import business.interactors.main.HomeInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.ui.main.home.view_model.HomeEvent
import presentation.ui.main.home.view_model.HomeState
import kotlin.random.Random

class HomeViewModel(
    private val homeInteractor: HomeInteractor,
    private val appDataStoreManager: AppDataStore
) : ViewModel() {
    val state: MutableState<HomeState> = mutableStateOf(HomeState())

    init {
        getChatMessages()
    }

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
        saveChatMessages(state.value.chatMessages)

        homeInteractor.sendMessage(message).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(HomeEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    onTriggerEvent(HomeEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        val gptMessage = ChatMessage(
                            id = generateRandomId(),
                            sender = "GPT",
                            content = it.message,
                            timestamp = getCurrentTimestamp()
                        )
                        addMessage(gptMessage)
                        saveChatMessages(state.value.chatMessages)
                    }
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
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

    fun getChatMessages() {
        viewModelScope.launch {
            val conversation = appDataStoreManager.readValue(DataStoreKeys.CHAT_CONVERSATION)
            conversation?.let {
                val messages = parseConversation(it)
                state.value = state.value.copy(chatMessages = messages)
            }
        }
    }

   private fun saveChatMessages(messages: List<ChatMessage>) {
        viewModelScope.launch {
            val conversation = formatConversation(messages)
            appDataStoreManager.setValue(DataStoreKeys.CHAT_CONVERSATION, conversation)
        }
    }

    private fun parseConversation(conversation: String): List<ChatMessage> {
        return try {
            Json.decodeFromString(conversation)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun formatConversation(messages: List<ChatMessage>): String {
        return try {
            Json.encodeToString(messages)
        } catch (e: Exception) {
            "" // Return an empty string in case of any formatting errors
        }
    }
}
