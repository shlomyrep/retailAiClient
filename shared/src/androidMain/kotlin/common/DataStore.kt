package common

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import business.core.APP_DATASTORE
import business.domain.main.DeviceData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

actual suspend fun Context.getData(key: String): String? {
    return dataStore.data.first()[stringPreferencesKey(key)] ?: ""
}

actual suspend fun Context.putData(key: String, `object`: String) {
    dataStore.edit {
        it[stringPreferencesKey(key)] = `object`
    }
}

actual fun Context.openNativeScreen(skuRegex: String, onScanResult: (String) -> Unit) {
    ScannerOpenerBridge.handleScanResult = onScanResult

    val intent = Intent(this, ScannerActivity::class.java).apply {
        putExtra("SKU_REGEX", skuRegex)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

actual fun Context.pdfOpener(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(url), "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No application found to open PDF.", Toast.LENGTH_SHORT).show()
    }
}

@SuppressLint("HardwareIds")
actual fun Context.deviceDataFetcher(scope: CoroutineScope, onDeviceDataFetched: (DeviceData) -> Unit) {
    DeviceDataBridge.getDeviceData?.invoke()

    scope.launch {
        val uuid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val name = Build.MODEL
        val deviceType = "Android"
        val lastInteractionTime = System.currentTimeMillis()
        val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode

        val deviceData = DeviceData(
            uuid = uuid,
            username = "username",
            name = name,
            version = getAppVersion(this@deviceDataFetcher),
            deviceType = deviceType,
            modelName = Build.MODEL,
            lastInteractionTime = lastInteractionTime,
            versionCode = versionCode
        )

        onDeviceDataFetched(deviceData)
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (e: Exception) {
        "Unknown"
    }
}





