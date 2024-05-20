package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.core.UIComponent
import business.datasource.network.main.MainService
import business.datasource.network.main.responses.ProductSelectable
import business.domain.main.SalesMan
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class AddBasketInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {


    fun execute(productSelectable: ProductSelectable, cartItemId:String): Flow<DataState<Boolean>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""

            val jsonSalesMan = appDataStoreManager.readValue(DataStoreKeys.SALES_MAN)
            val user = jsonSalesMan?.let { Json.decodeFromString(SalesMan.serializer(), it) }

            val apiResponse = service.basketAdd(
                token = token,
                productSelectable = productSelectable,
                cartItemId = cartItemId,
                salesMan = user ?: SalesMan()
            )
            apiResponse.alert?.let { alert ->
                emit(
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            alert = alert
                        )
                    )
                )
            }
            emit(DataState.NetworkStatus(NetworkState.Good))
            emit(DataState.Data(apiResponse.status))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}