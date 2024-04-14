package common

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.allViews
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.razzaghi.shopingbykmp.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity() {

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private var processingBarcode = false
    private lateinit var scannerViewModel: ScannerViewModel
    private lateinit var progressBar: ProgressBar

    //    private var storeSkuData = mapOf<String, SupplierSku>()
    private val listOfPlayedSound = mutableListOf<String>()
    private val listOfScanBarcodes = mutableListOf<String>()
    private var isProcessingEnabled = true
    private var animator: ObjectAnimator? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var barcodeBtnLl: LinearLayout

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        barcodeBtnLl = findViewById(R.id.barcodeButtonContainer)
        scannerViewModel = ViewModelProvider(this)[ScannerViewModel::class.java]
        scannerViewModel.loadSkuStore()
        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (isCameraPermissionGranted()) {
            startCamera(barcodeBtnLl)
        } else {
            requestCameraPermission()
        }
        setObserver()
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        "You must approve camera usage in order to scan products",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdownNow()
    }

    private fun setObserver() {
        resetButtonGeneration()

        scannerViewModel.failedFallbackState.observe(this) {
            for (allView in barcodeBtnLl.allViews) {
                if (allView is ViewGroup && allView.childCount > 0) {
                    // Check if the first child of this ViewGroup is also a ViewGroup
                    val firstChild = allView.getChildAt(0)
                    if (firstChild is ViewGroup && firstChild.childCount > 0) {
                        // Now, safely cast the first child of the inner ViewGroup, checking if it's a TextView
                        val textView = firstChild.getChildAt(0)
                        val progressBar = firstChild.getChildAt(1)
                        if (textView is TextView && textView.text.contains(it)) {
                            textView.visibility = View.VISIBLE
                            textView.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        scannerViewModel.isFallbackProductAdded.observe(this) {
            if (it) {
//                navigateToPagerFragment(scanQrViewModel.tempProductId, scanQrViewModel.tempSku)
            }
        }
        scannerViewModel.fetchFallbackSuccessfully.observe(this) {
            if (!it) {
                Toast.makeText(this, "Failed to fetch fallback product", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }


    private fun startCamera(container: LinearLayout) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            preview = Preview.Builder().build()
            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        ImageAnalysis.Analyzer { imageProxy ->
                            if (processingBarcode || !isProcessingEnabled) {
                                imageProxy.close()
                                return@Analyzer
                            }
                            processingBarcode = true
                            processImage(container, imageProxy)
                        })
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

            preview.setSurfaceProvider(previewView.surfaceProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImage(container: LinearLayout?, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        createBarcodeButtons(barcodes, container)
                        animator?.cancel()
                    }
                }
                .addOnFailureListener {
                    Log.e("ScannerActivity", "Error processing image", it)
                }
                .addOnCompleteListener {
                    processingBarcode = false
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun handleBarcodeClick(barcode: Barcode) {
        handleSelectedBarcode(barcode)
    }

    private fun handleSelectedBarcode(barcode: Barcode) {
        val rawValue = barcode.rawValue
        if (rawValue != null) {
            ScannerOpenerBridge.handleScanResult?.invoke(rawValue)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createBarcodeButtons(
        barcodes: List<Barcode>,
        container: LinearLayout?
    ) {
//        vibratePhone(requireContext())
        isProcessingEnabled = false
        val maxButtons = 7
        barcodes.take(maxButtons).forEach { barcode ->
            if (barcode.rawValue?.length!! > 7) {
                // Create a FrameLayout to hold the TextView and ProgressBar
                val buttonLayout = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        val margin = dpToPx(7)
                        setMargins(margin, margin, margin, margin)
                    }
                }

                // Create your TextView (barcodeButton)
                val barcodeButton = TextView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity =
                            Gravity.CENTER // Ensures the TextView is centered in the FrameLayout
                    }
                    elevation = 1.5f
                    text = "${barcode.displayValue}"
                    textSize = 18f
                    setTextColor(Color.BLACK)
                    typeface = ResourcesCompat.getFont(this@ScannerActivity, R.font.lato_regular)
                    setBackgroundResource(R.drawable.button_ripple)
                    setPadding(dpToPx(80), dpToPx(15), dpToPx(80), dpToPx(15))
                }

                // Create a ProgressBar and initially hide it
                progressBar = ProgressBar(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity =
                            Gravity.CENTER // Ensures the ProgressBar is centered in the FrameLayout
                    }
                    visibility = View.GONE // Initially hide the ProgressBar

                    indeterminateDrawable.setColorFilter(
                        Color.parseColor("#1b6682"), // Use Color.parseColor to convert hex to an int color
                        PorterDuff.Mode.SRC_IN
                    )
                }
                buttonLayout.addView(barcodeButton)
                buttonLayout.addView(progressBar)
                barcodeButton.setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    barcodeButton.visibility = View.INVISIBLE
                    // Optionally hide the text of the barcodeButton or make it look disabled
                    barcodeButton.isEnabled = false
                    handleBarcodeClick(barcode) // Your click handling logic
                    container?.addView(buttonLayout)
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = this.resources?.displayMetrics?.density ?: 1f
        return (dp * density).toInt()
    }

    private fun resetButtonGeneration() {
        barcodeBtnLl.removeAllViews()
        isProcessingEnabled = true
    }
}


