package common

import business.domain.main.PlatformData
import kotlinx.coroutines.CoroutineScope
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


actual fun Context.openNativeScreen(skuRegex: String, onScanResult: (String) -> Unit) {
    ScannerOpenerBridge.openScannerScreenFunc?.invoke(skuRegex)
    ScannerOpenerBridge.handleScanResult = onScanResult
    NSLog("TAMIR IOS --> Scan result back in common: $onScanResult")
}

actual fun Context.pdfOpener(url: String) {
    PdfOpenerBridge.openPdfFunc?.invoke(url)

}

actual fun Context.deviceDataFetcher(scope: CoroutineScope, onDeviceDataFetched: (PlatformData) -> Unit) {
    println("KOTLIN --> deviceDataFetcher invoked")

    // Set the callback before invoking the iOS method to ensure it's available
    DeviceDataBridge.handleDeviceDataResult = { platformData ->
        println("KOTLIN --> handleDeviceDataResult received: ${platformData.uuid}")
        onDeviceDataFetched(platformData) // This is where we pass the data to the higher-level Kotlin layer
    }

    // Invoke the fetch on iOS, expecting the callback to be called back
    DeviceDataBridge.getDeviceData?.invoke()
    println("KOTLIN --> getDeviceData invoked on iOS")
}

