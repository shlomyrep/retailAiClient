package business.core

import common.Context
import common.getData
import common.openNativeScreen
import common.putData

const val APP_DATASTORE = "com.razzaghi.shoppingbykmp"

class AppDataStoreManager(val context: Context) : AppDataStore {

    override suspend fun setValue(
        key: String,
        value: String
    ) {
        context.putData(key, value)
    }

    override suspend fun readValue(
        key: String,
    ): String? {
        return context.getData(key)
    }

    override fun openActivity() {
        context.openNativeScreen()
    }
}