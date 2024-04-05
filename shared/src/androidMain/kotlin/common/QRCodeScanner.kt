package common

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

actual class QRCodeScanner actual constructor() {
    private lateinit var activity: AppCompatActivity
    private lateinit var contentScanResult: (Result<String>) -> Unit
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    fun initialize(activity: AppCompatActivity) {
        this.activity = activity
        resultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult.contents != null) {
                contentScanResult(Result.success(intentResult.contents))
            } else {
                contentScanResult(Result.failure(Exception("QR Code scanning failed or was cancelled.")))
            }
        }
    }

    actual fun scanQRCode(onResult: (Result<String>) -> Unit) {
        if (!this::activity.isInitialized || !this::resultLauncher.isInitialized) {
            onResult(Result.failure(IllegalStateException("QRCodeScanner is not initialized")))
            return
        }
        this.contentScanResult = onResult
        val integrator = IntentIntegrator(activity).apply {
            setOrientationLocked(false)
            setPrompt("Scan a QR code")
        }
        val intent = integrator.createScanIntent()
        resultLauncher.launch(intent)
    }
}


