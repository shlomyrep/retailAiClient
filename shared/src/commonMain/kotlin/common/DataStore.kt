package common

expect suspend fun Context.putData(key: String, `object`: String)

expect suspend fun Context.getData(key: String): String?

expect fun Context.openNativeScreen()

object ScannerOpenerBridge {
    var openScannerScreenFunc: (() -> Unit)? = null
    var handleScanResult: ((String) -> Unit)? = null
}