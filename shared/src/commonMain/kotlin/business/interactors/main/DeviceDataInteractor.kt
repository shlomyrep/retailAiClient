package business.interactors.main


import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.ProgressBarState
import business.datasource.network.common.JRNothing
import business.datasource.network.main.MainService
import business.domain.main.DeviceData
import business.util.handleUseCaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeviceDataInteractor(
    private val service: MainService,
    private val appDataStoreManager: AppDataStore,
) {

    fun execute(
        uuid: String,
        username: String,
        name: String,
        version: String,
        deviceType: String,
        modelName: String,
        lastInteractionTime: Long
    ): Flow<DataState<JRNothing>> = flow {
        try {
            println("DeviceData, execute 1")
            emit(DataState.Loading(progressBarState = ProgressBarState.LoadingWithLogo))
            val token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
            val deviceData = DeviceData(
                uuid = uuid,
                username = username,
                name = name,
                version = version,
                fcm = "",
                deviceType = deviceType,
                modelName = modelName,
                lastInteractionTime = lastInteractionTime
            )
            println("DeviceData, execute 2")
            val apiResponse = service.sendClientData(token = token, deviceData = deviceData)
            println("DeviceData, execute 3")
//            if (apiResponse.status == false || apiResponse.result == null) {
//                throw Exception(apiResponse.alert?.createException())
//            }

            emit(DataState.NetworkStatus(NetworkState.Good))
            emit(DataState.Data(apiResponse.result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.NetworkStatus(NetworkState.Failed))
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }.flowOn(Dispatchers.IO) // Make sure to run it on the correct dispatcher

}