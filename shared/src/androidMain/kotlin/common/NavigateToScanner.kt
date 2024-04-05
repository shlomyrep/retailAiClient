package common

import android.content.Intent


actual class NavigateToScanner actual constructor(context: Context) {

    private val mContext = context
    actual fun navigate() {
        val intent = Intent(mContext, ScannerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        mContext.startActivity(intent)
    }
}

