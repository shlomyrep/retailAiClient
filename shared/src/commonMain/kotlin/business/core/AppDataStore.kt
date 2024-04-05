package business.core

import common.ScannerResultListener


interface AppDataStore {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

    fun openActivity(barcodeScannerListener: ScannerResultListener)
}