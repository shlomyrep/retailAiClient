package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.main.MainService
import business.datasource.network.main.responses.ProductSelectable
import business.domain.main.SalesMan
import business.util.createException
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class GetEditOrderInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {

    fun execute(orderId: String): Flow<DataState<List<ProductSelectable>>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))

            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val jsonSalesMan = appDataStoreManager.readValue(DataStoreKeys.SALES_MAN)
            val user = jsonSalesMan?.let { Json.decodeFromString(SalesMan.serializer(), it) }

            val apiResponse = service.editOrder(
                token = token,
                user = user?.username,
                orderId = orderId
            )

            if (apiResponse.status == false) {
                throw Exception(
                    apiResponse.alert?.createException()
                )
            }

            emit(DataState.NetworkStatus(NetworkState.Good))
            emit(DataState.Data(apiResponse.result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.NetworkStatus(NetworkState.Failed))
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}