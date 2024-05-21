package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.main.MainService
import business.datasource.network.main.responses.toHome
import business.domain.main.CustomerConfig
import business.domain.main.Home
import business.util.createException
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class HomeInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {

    fun execute(): Flow<DataState<Home>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))

            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""

            val apiResponse = service.home(token = token)

            if (apiResponse.status == false || apiResponse.result == null) {
                throw Exception(
                    apiResponse.alert?.createException()
                )
            }
            val configResult = apiResponse.result?.config

            if (configResult != null) {
                val jsonConfig = Json.encodeToString(CustomerConfig.serializer(), configResult)
                appDataStoreManager.setValue(DataStoreKeys.CUSTOMER_CONFIG, jsonConfig)
            }

            val result = apiResponse.result?.toHome()


            emit(DataState.NetworkStatus(NetworkState.Good))
            emit(DataState.Data(result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.NetworkStatus(NetworkState.Failed))
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}