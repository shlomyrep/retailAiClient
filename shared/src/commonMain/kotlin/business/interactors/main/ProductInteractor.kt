package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.main.MainService
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.toHeldInventoryBatch
import business.domain.main.HeldInventoryBatch
import business.util.createException
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {


    fun execute(id: String): Flow<DataState<ProductSelectable>> = flow {

        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val apiResponse = service.product(token = token, id = id)
            if (apiResponse.status == false || apiResponse.result == null) {
                throw Exception(
                    apiResponse.alert?.createException()
                )
            }
            val result = apiResponse.result
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


    fun getProductInventory(supplierId: String, sku: String): Flow<DataState<HeldInventoryBatch>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val apiResponse = service.productInventory(token = token, supplierId = supplierId, sku = sku)
            val result = apiResponse?.toHeldInventoryBatch()
            emit(DataState.NetworkStatus(NetworkState.Good))
            emit(DataState.Data(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}