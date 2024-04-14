package common

import kotlinx.coroutines.flow.MutableSharedFlow
import platform.Foundation.NSLog
import platform.Foundation.NSUserDefaults


actual suspend fun Context.putData(key: String, `object`: String) {
    val sharedFlow = MutableSharedFlow<String>()
    NSUserDefaults.standardUserDefaults().setObject(`object`, key)
    sharedFlow.emit(`object`)
}

actual suspend inline fun Context.getData(key: String): String? {
    return NSUserDefaults.standardUserDefaults().stringForKey(key)
}


actual fun Context.openNativeScreen() {
    ScannerOpenerBridge.openScannerScreenFunc?.invoke()

    NSLog("TAMIR --> openNativeScreen")
    println("TAMIR --> openNativeScreen")
}

object ScannerOpenerBridge {
    var openScannerScreenFunc: (() -> Unit)? = null
    var handleScanResult: ((String) -> Unit)? = { result ->
        NSLog("TAMIR  תמיר--> Scan result יש לנו תשובה חזרה: $result")
    }
}
