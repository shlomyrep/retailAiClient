package common

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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
import com.digitup.retailai.android.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.serialization.json.Json


class ScannerActivity : AppCompatActivity() {
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private var isProcessingEnabled = true
    private var numberOccurrencesMap = mutableMapOf<String, Int>()
    private lateinit var previewView: PreviewView
    private lateinit var barcodeButtonContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private var skuRegex: String = ""
    private var skuRegexList: List<String> = listOf()

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        previewView = findViewById(R.id.previewView)
        barcodeButtonContainer = findViewById(R.id.barcodeButtonContainer)
        progressBar = findViewById(R.id.progressBar)

        skuRegex = intent.getStringExtra("SKU_REGEX") ?: "[]"
         skuRegexList = Json.decodeFromString(skuRegex)

        Log.i("TAMIRRRR", "Parsed skuRegexList: $skuRegexList")
        Log.i("TAMIRRRR", "onCreate: $skuRegex")

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE
            )
        }
        setObservers()
    }

    private fun setObservers() {

    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            setupImageAnalysis()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupImageAnalysis() {
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            if (isProcessingEnabled) {
                processImage(imageProxy)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val barcodeScanner = BarcodeScanning.getClient()
            val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            textRecognizer.process(image)
                .addOnSuccessListener { texts ->
                    if (texts.textBlocks.isNotEmpty()) {
                        handleTextRecognitionResult(texts)
                    }
                }

            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        createBarcodeButtons(barcodes)
                    }
                }
                .addOnFailureListener {
                    // Handle any errors
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun resetButtonGeneration() {
        barcodeButtonContainer.removeAllViews()
        isProcessingEnabled = true
    }

    private fun handleTextRecognitionResult(texts: com.google.mlkit.vision.text.Text) {
        val matchedTexts = texts.textBlocks.mapNotNull { it.text }
            .filter { text ->
                // Check against all regex patterns
                skuRegexList.any { pattern -> Regex(pattern).matches(text) }
            }

        matchedTexts.forEach { matchedText ->
            val count = numberOccurrencesMap[matchedText] ?: 0
            numberOccurrencesMap[matchedText] = count + 1

            if ((numberOccurrencesMap[matchedText] ?: 0) >= 3) {
                createTextButton(matchedText)
                numberOccurrencesMap[matchedText] = 0
            }
        }
    }


    private fun createTextButton(text: String) {
        val existingButton = barcodeButtonContainer.findViewWithTag<TextView>(text)
        if (existingButton != null) {
            return
        }

        val buttonLayout = createButtonLayout()

        val textButton = TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            elevation = 1.5f
            val newText = "${"פתח מוצר"} $text"
            setText(newText)
            textSize = 18f
            setTextColor(Color.BLACK)
            typeface = ResourcesCompat.getFont(this@ScannerActivity, R.font.lato_regular)
            setBackgroundResource(R.drawable.button_ripple)
            setPadding(dpToPx(80), dpToPx(15), dpToPx(80), dpToPx(15))
            tag = text

            setOnClickListener {
                handleBarcodeClick(text)
            }
        }
        buttonLayout.addView(textButton)
        barcodeButtonContainer.addView(buttonLayout)
    }

    private fun createBarcodeButtons(barcodes: List<Barcode>) {
        isProcessingEnabled = false
        barcodes.forEach { barcode ->
            val barcodeValue = barcode.rawValue ?: ""

            // Check against all regex patterns
            if (skuRegexList.any { pattern -> Regex(pattern).matches(barcodeValue) }) {
                val buttonLayout = createButtonLayout()

                val barcodeButton = TextView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                    elevation = 1.5f
                    text = " פתח מוצר $barcodeValue"
                    textSize = 18f
                    setTextColor(Color.BLACK)
                    typeface = ResourcesCompat.getFont(this@ScannerActivity, R.font.lato_regular)
                    setBackgroundResource(R.drawable.button_ripple)
                    setPadding(dpToPx(80), dpToPx(15), dpToPx(80), dpToPx(15))
                }
                barcodeButton.setOnClickListener {
                    barcodeButton.visibility = View.INVISIBLE
                    barcodeButton.isEnabled = false
                    handleBarcodeClick(barcodeValue)
                }
                buttonLayout.addView(barcodeButton)
                barcodeButtonContainer.addView(buttonLayout)
            }
        }
    }



    private fun createButtonLayout(): FrameLayout {
        return FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = dpToPx(7)
                setMargins(margin, margin, margin, margin)
            }
        }
    }

    private fun handleBarcodeClick(barcode: String) {
        ScannerOpenerBridge.handleScanResult?.invoke(barcode)
        resetButtonGeneration()
        finish()
    }

    private fun dpToPx(dp: Int): Int {
        val density = this.resources?.displayMetrics?.density ?: 1f
        return (dp * density).toInt()
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}


