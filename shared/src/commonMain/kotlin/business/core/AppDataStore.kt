package business.core


interface AppDataStore {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

    fun openActivity(onScanResult: (String) -> Unit)
    fun openPdfUrl(url:String)
}