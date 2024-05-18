package common

expect suspend fun Context.putData(key: String, `object`: String)

expect suspend fun Context.getData(key: String): String?

//expect fun Context.openNativeScreen(onScanResult: (String) -> Unit)
expect fun Context.openNativeScreen(skuRegex: String, onScanResult: (String) -> Unit)



object ScannerOpenerBridge {
    var openScannerScreenFunc: ((String) -> Unit)? = null
    var handleScanResult: ((String) -> Unit)? = null
}

expect fun Context.pdfOpener(url:String)

object PdfOpenerBridge {
    var openPdfFunc: ((String) -> Unit)? = null
}

