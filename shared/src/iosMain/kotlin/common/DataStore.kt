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
//fun Context.openNativeScreen(onScanResult: (String) -> Unit) {


actual fun Context.openNativeScreen(onScanResult: (String) -> Unit) {
    ScannerOpenerBridge.openScannerScreenFunc?.invoke()
    ScannerOpenerBridge.handleScanResult = onScanResult
}
