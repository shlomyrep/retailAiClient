package common

// In commonMain/kotlin/QRCodeScanner.kt
// commonMain/kotlin/QRCodeScanner.kt
expect class QRCodeScanner() {
    fun scanQRCode(onResult: (Result<String>) -> Unit)
}

