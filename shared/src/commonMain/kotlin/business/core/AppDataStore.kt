package business.core


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
}