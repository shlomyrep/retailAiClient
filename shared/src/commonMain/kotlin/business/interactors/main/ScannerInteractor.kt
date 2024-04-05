package business.interactors.main


import business.core.AppDataStore
import business.datasource.network.main.MainService

class ScannerInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
 ) {


//    fun execute(): Flow<DataState<Home>> = flow {
//
//        try {
//
//            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
//
//            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
//
//
//            val apiResponse = service.home(token = token)
//
//
//
//            if (apiResponse.status == false || apiResponse.result == null) {
//                throw Exception(
//                    apiResponse.alert?.createException()
//                )
//            }
//
//
//            val result = apiResponse.result?.toHome()
//
//
//            emit(DataState.NetworkStatus(NetworkState.Good))
//            emit(DataState.Data(result))
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emit(DataState.NetworkStatus(NetworkState.Failed))
//            emit(handleUseCaseException(e))
//
//        } finally {
//            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
//        }
//
//
//    }


}