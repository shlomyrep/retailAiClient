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


actual fun Context.openNativeScreen(onScanResult: (String) -> Unit) {
    ScannerOpenerBridge.openScannerScreenFunc?.invoke()
    ScannerOpenerBridge.handleScanResult = onScanResult
    NSLog("TAMIR IOS --> Scan result back in common: $onScanResult")
}

actual fun Context.pdfOpener(url: String) {
    PdfOpenerBridge.openPdfFunc?.invoke(url)
}
