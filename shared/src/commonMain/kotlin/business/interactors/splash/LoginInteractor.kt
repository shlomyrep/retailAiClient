package business.interactors.splash


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.ProgressBarState
import business.core.UIComponent
import business.datasource.network.splash.SplashService
import business.domain.main.SalesMans
import business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginInteractor(
    private val service: SplashService,
    private val appDataStoreManager: AppDataStore,
) {
    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<SalesMans>> = flow {

        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.ButtonLoading))
            val apiResponse = service.login(email, password)

            val token = apiResponse.token
            val users = apiResponse.result
            if (token != null) {
                appDataStoreManager.setValue(
                    DataStoreKeys.TOKEN,
                    apiResponse.token.toString()
                )
                appDataStoreManager.setValue(
                    DataStoreKeys.EMAIL,
                    email
                )
            } else {
                emit(
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            alert = apiResponse.alert!!
                        )
                    )
                )
            }
            emit(DataState.Data(users, apiResponse.status))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}