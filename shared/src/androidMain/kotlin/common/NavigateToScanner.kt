package common

import android.widget.Toast


actual class NavigateToScanner actual constructor(context: Context) {

    private val mContext = context
    actual fun navigate() {
//        val intent = Intent(mContext, ScannerActivity::class.jave)
//        mContext.startActivity(intent)

        Toast.makeText(mContext, "Fuck this shit!!!!!!", Toast.LENGTH_LONG).show()
    }
}

