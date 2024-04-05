package common
object EventBus {
    private var listener: ScannerResultListener? = null

    fun setListener(listener: ScannerResultListener?) {
        this.listener = listener
    }

    fun postResult(result: String) {
        listener?.onResult(result)
    }
}

interface ScannerResultListener {
    fun onResult(result: String)
}