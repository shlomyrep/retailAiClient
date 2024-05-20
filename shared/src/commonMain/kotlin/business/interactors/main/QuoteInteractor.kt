package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.main.MainService
import business.domain.main.OrderResponse
import business.domain.main.Quote
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QuoteInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {
    fun execute(quote: Quote): Flow<DataState<OrderResponse>> = flow {

        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val apiResponse = service.sendQuote(token = token, quote = quote)

            emit(DataState.NetworkStatus(NetworkState.Good))
            emit(DataState.Data(apiResponse.result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}