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
import business.domain.main.PlatformData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

actual suspend fun Context.getData(key: String): String? {
    return dataStore.data.first()[stringPreferencesKey(key)] ?: ""
}

actual suspend fun Context.putData(key: String, `object`: String) {
    dataStore.edit {
        it[stringPreferencesKey(key)] = `object`
    }
}

actual fun Context.openNativeScreen(skuRegex: List<String>, onScanResult: (String) -> Unit) {
    ScannerOpenerBridge.handleScanResult = onScanResult

    // Convert the List<String> to JSON
    val skuRegexJson = Json.encodeToString(skuRegex)

    val intent = Intent(this, ScannerActivity::class.java).apply {
        putExtra("SKU_REGEX", skuRegexJson)
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
actual fun Context.deviceDataFetcher(scope: CoroutineScope, onDeviceDataFetched: (PlatformData) -> Unit) {
    DeviceDataBridge.getDeviceData?.invoke()

    scope.launch {
        val uuid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val name = Build.MODEL
        val deviceType = "Android"

        val platformData = PlatformData(
            uuid = uuid,
            version = getAppVersion(this@deviceDataFetcher),
            name = name,
            deviceType = deviceType,
            modelName = Build.MODEL,
        )
        onDeviceDataFetched(platformData)
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





