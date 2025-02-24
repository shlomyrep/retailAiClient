package business.core

import business.domain.main.PlatformData
import common.Context
import common.deviceDataFetcher
import common.getData
import common.openNativeScreen
import common.pdfOpener
import common.putData
import kotlinx.coroutines.CoroutineScope

const val APP_DATASTORE = "com.digitup.retailai"

class AppDataStoreManager(val context: Context) : AppDataStore {

    override suspend fun setValue(
        key: String,
        value: String
    ) {
        context.putData(key, value)
    }

    override suspend fun readValue(
        key: String,
    ): String? {
        return context.getData(key)
    }

    override fun openActivity(skuRegex: List<String>, onScanResult: (String) -> Unit) {
        context.openNativeScreen(skuRegex,onScanResult)
    }

    override fun openPdfUrl(url: String) {
        context.pdfOpener(url)
    }

    override fun fetchDeviceData(scope: CoroutineScope, onDeviceDataFetched: (PlatformData) -> Unit) {
        context.deviceDataFetcher(scope, onDeviceDataFetched)
    }
}