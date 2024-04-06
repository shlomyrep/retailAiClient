package common

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun NavigateToScanner() {
    val context = LocalContext.current

    val intent = Intent(context, ScannerActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}


