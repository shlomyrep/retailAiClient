package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.UIComponent
import business.datasource.network.main.MainService
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LikeInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {


    fun execute(id: String): Flow<DataState<Boolean>> = flow {

        try {

           // emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))

            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""


            val apiResponse = service.like(
                token = token,
                id = id
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


             emit(DataState.Data(apiResponse.status))

        } catch (e: Exception) {
            e.printStackTrace()
             emit(handleUseCaseException(e))

        } finally {
         //   emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }


    }


}