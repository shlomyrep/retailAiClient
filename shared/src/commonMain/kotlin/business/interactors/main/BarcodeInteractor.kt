package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.main.MainService
import business.datasource.network.main.responses.ProductSelectable
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BarcodeInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {


    fun execute(barcode: String): Flow<DataState<ProductSelectable>> = flow {

        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val apiResponse = service.productBySku(token = token, sku = barcode)
            if (apiResponse.status == false || apiResponse.result == null) {
//                throw Exception(
//                    apiResponse.alert?.createException()
//                )
            }
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