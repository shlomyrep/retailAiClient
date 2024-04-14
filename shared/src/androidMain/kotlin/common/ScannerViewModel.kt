package common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ScannerViewModel: ViewModel() {

    var isFallbackProductAdded = MutableLiveData<Boolean>()
//    val storeSkuLiveData = MutableLiveData<StoreSku?>()
    val failedFallbackState = MutableLiveData<String>()
    val fetchFallbackSuccessfully = MutableLiveData<Boolean>()
//    private val repo = KitCatRepo
    var tempProductId = ""
    var tempSku = ""


    fun loadSkuStore() {
        viewModelScope.launch {
//            val storeSkus = repo.getStoreSku()
//            storeSkuLiveData.postValue(storeSkus)
        }
    }

//    fun sendSkuToServer(sku: String) {
//        try {
//            viewModelScope.launch(Dispatchers.IO) {
//                val call =
//                    AppRetrofitService.getRetrofit(BuildConfig.KITCAT_URL_BASE)
//                        .create(KitCatApi::class.java).postSkuToServer(sku)
//                val response: Response<Void> = call.execute()
//                if (response.isSuccessful) {
//                    Log.i(
//                        "sendSkuToServer",
//                        "sendSkuToServer: isSuccessful = ${response.isSuccessful} sku = $sku"
//                    )
//                }
//            }
//        } catch (t: Throwable) {
//            Log.e("sendSkuToServer", "Error in sending Sku Data: ${t.message}", t)
//        }
//    }

//    fun fetchFallbackProduct(sku: String) {
//        try {
//            isFallbackProductAdded.value = false
//            viewModelScope.launch(Dispatchers.IO) {
//                val call = AppRetrofitService.getRetrofit(BuildConfig.KITCAT_URL_BASE)
//                    .create(KitCatApi::class.java).sendFallBackProductSkuToServer(sku)
//                val response: Response<ProductSelectable> = call.execute()
//                if (response.isSuccessful) {
//                    val fallbackProduct = response.body()
//                    repo.addProductToCachedProducts(fallbackProduct)
//                    isFallbackProductAdded.postValue(true)
//                    tempProductId = fallbackProduct?._id.toString()
//                    tempSku = sku
//                    fallbackProduct?.sku = sku
//                    fetchFallbackSuccessfully.postValue(true)
//                } else {
//                    // Notify the user of the failure
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(
//                            MainApplication.appContext,
//                            "כישלון בפתיחת מוצר",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    failedFallbackState.postValue(sku)
//                    fetchFallbackSuccessfully.postValue(false)
//                }
//            }
//        } catch (t: Throwable) {
//            fetchFallbackSuccessfully.postValue(false)
//            Log.e("fetchFallbackProduct", "Error in fetching fallback product: ${t.message}", t)
//        }
//    }
}