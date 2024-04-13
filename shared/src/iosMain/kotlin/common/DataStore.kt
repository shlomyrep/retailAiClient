package common

import kotlinx.coroutines.flow.MutableSharedFlow
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

//    val scanner2 = ScannerViewController().apply{}
}

object ScannerOpenerBridge {

    var openScannerScreenFunc: (() -> Unit)? = null

}


//    println("1 TAMIR TEST")
//    val scanner = UIImagePickerController().apply {
//        println("2 TAMIR TEST")
//        sourceType =  UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
//        cameraCaptureMode = UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto
//        delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol,
//            UINavigationControllerDelegateProtocol {
//            override fun imagePickerController(
//                picker: UIImagePickerController,
//                didFinishPickingMediaWithInfo: Map<Any?, *>
//            ) {
//                val originalImage = didFinishPickingMediaWithInfo.getValue(
//                    UIImagePickerControllerOriginalImage
//                ) as? UIImage
//
//                originalImage?.let { image ->
//                    // Convert image to JPEG data
//                    val data = UIImageJPEGRepresentation(image, 1.0)
//
//                    // Save to documents directory
//                    val path = NSSearchPathForDirectoriesInDomains(
//                        NSDocumentDirectory,
//                        NSUserDomainMask,
//                        true
//                    ).first().toString()
//                    val filePath = "$path/" + NSUUID.UUID().UUIDString + ".jpg"
//
//                }
//                picker.dismissViewControllerAnimated(true, null)
//            }
//        }
//    }
//    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
//        scanner, true, null
//    )
//    println("3 TAMIR TEST")