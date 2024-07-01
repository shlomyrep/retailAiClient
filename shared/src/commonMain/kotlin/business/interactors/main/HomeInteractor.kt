package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.main.MainService
import business.domain.main.ChatGptRequest
import business.domain.main.ChatGptResponse
import business.domain.main.Home
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {

    fun execute(): Flow<DataState<Home>> = flow {

    }

    fun sendMessage(message: String): Flow<DataState<ChatGptResponse>> = flow {

        try {
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val chatGptRequest = ChatGptRequest(message)
            val apiResponse = service.home(token = token, chatGptRequest = chatGptRequest)
            emit(DataState.Data(apiResponse))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.NetworkStatus(NetworkState.Failed))
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}