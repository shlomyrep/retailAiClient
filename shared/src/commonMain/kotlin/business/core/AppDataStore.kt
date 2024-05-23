package business.core

import business.domain.main.DeviceData
import kotlinx.coroutines.CoroutineScope


interface AppDataStore {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

    fun openActivity(skuRegex: String,onScanResult: (String) -> Unit)
    fun openPdfUrl(url:String)
    fun fetchDeviceData(scope: CoroutineScope, onDeviceDataFetched: (DeviceData) -> Unit)
}