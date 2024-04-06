package common

import kotlinx.coroutines.flow.MutableSharedFlow
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController


actual suspend fun Context.putData(key: String, `object`: String) {
    val sharedFlow = MutableSharedFlow<String>()
    NSUserDefaults.standardUserDefaults().setObject(`object`, key)
    sharedFlow.emit(`object`)
}

actual suspend inline fun Context.getData(key: String): String? {
    return NSUserDefaults.standardUserDefaults().stringForKey(key)
}

// Inside iosMain source set
actual fun Context.openNativeScreen(listener: ScannerResultListener) {
//    val vc = ScannerViewController() // Initialize your ScannerViewController
//    vc.didFindCode = { result ->
//        listener.onResult(result)
//    }
//    // You need to present the view controller from the current context
//    (this as? UIViewController)?.presentingViewController()
}