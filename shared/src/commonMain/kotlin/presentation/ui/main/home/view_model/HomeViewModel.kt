package presentation.ui.main.home.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.CUSTOM_TAG
import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.CustomerConfig
import business.domain.main.SalesMan
import business.interactors.main.DeviceDataInteractor
import business.interactors.main.HomeInteractor
import business.interactors.main.LikeInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeViewModel(
    private val deviceDataInteractor: DeviceDataInteractor,
    private val homeInteractor: HomeInteractor,
    private val likeInteractor: LikeInteractor,
    private val appDataStoreManager: AppDataStore,
) : ViewModel() {


    val state: MutableState<HomeState> = mutableStateOf(HomeState())

    fun onTriggerEvent(event: HomeEvent) {
        when (event) {

            is HomeEvent.Like -> {
                likeProduct(id = event.id)
            }

            is HomeEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }

            is HomeEvent.Error -> {
                appendToMessageQueue(event.uiComponent)
            }

            is HomeEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is HomeEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
    }

    init {
        getHome()
        getDeviceData()
    }


    private fun likeProduct(id: String) {
        likeInteractor.execute(id = id)
            .onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onTriggerEvent(HomeEvent.Error(dataState.uiComponent))
                    }

                    is DataState.Data -> {
                        dataState.data?.let {
                            if (it) updateLike(id)
                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun updateLike(id: String) {

        updateMostSaleProductLike(id = id)
        updateNewestProductLike(id = id)
        updateFlashSaleProductLike(id = id)

    }

    private fun updateFlashSaleProductLike(id: String) {

        val tmpFlashSale = state.value.home.flashSale.products.toMutableList()
        var currentItemFlashSale = tmpFlashSale.find { it.id == id }
        val indexCurrentItemFlashSale = tmpFlashSale.indexOf(currentItemFlashSale)
        val newLikes3 =
            if (currentItemFlashSale?.isLike == true) currentItemFlashSale.likes?.minus(1) else currentItemFlashSale?.likes?.plus(
                1
            )
        currentItemFlashSale = currentItemFlashSale?.copy(
            isLike = !currentItemFlashSale.isLike,
            likes = newLikes3 ?: 0
        )
        if (currentItemFlashSale != null) {
            tmpFlashSale[indexCurrentItemFlashSale] = currentItemFlashSale
        }

        state.value =
            state.value.copy(
                home = state.value.home.copy(
                    flashSale = state.value.home.flashSale.copy(
                        products = tmpFlashSale
                    )
                )
            )
    }

    private fun updateNewestProductLike(id: String) {
        val tmpNewestProduct = state.value.home.newestProduct.toMutableList()
        var currentItemNewestProduct = tmpNewestProduct.find { it.id == id }
        val indexCurrentItemNewestProduct = tmpNewestProduct.indexOf(currentItemNewestProduct)

        val newLikes2 =
            if (currentItemNewestProduct?.isLike == true) currentItemNewestProduct.likes?.minus(1) else currentItemNewestProduct?.likes?.plus(
                1
            )

        currentItemNewestProduct = currentItemNewestProduct?.copy(
            isLike = !currentItemNewestProduct.isLike,
            likes = newLikes2 ?: 0
        )


        if (currentItemNewestProduct != null) {
            tmpNewestProduct[indexCurrentItemNewestProduct] = currentItemNewestProduct
        }

        state.value =
            state.value.copy(home = state.value.home.copy(newestProduct = tmpNewestProduct))
    }

    private fun updateMostSaleProductLike(id: String) {
        val tmpMostSale = state.value.home.mostSale.toMutableList()
        var currentItemMostSale = tmpMostSale.find { it.id == id }
        val indexCurrentItemMostSale = tmpMostSale.indexOf(currentItemMostSale)

        val newLikes1 =
            if (currentItemMostSale?.isLike == true) currentItemMostSale.likes?.minus(1) else currentItemMostSale?.likes?.plus(
                1
            )

        currentItemMostSale =
            currentItemMostSale?.copy(isLike = !currentItemMostSale.isLike, likes = newLikes1 ?: 0)

        if (currentItemMostSale != null) {
            tmpMostSale[indexCurrentItemMostSale] = currentItemMostSale
        }

        state.value =
            state.value.copy(home = state.value.home.copy(mostSale = tmpMostSale))
    }


    private fun getHome() {
        homeInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(HomeEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    onTriggerEvent(HomeEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        val currentDateTime =
                            Instant.parse(it.flashSale.expiredAt).toLocalDateTime(TimeZone.UTC)
                        state.value = state.value.copy(home = it)
                        state.value = state.value.copy(time = currentDateTime)
                    }
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) {
            println("${CUSTOM_TAG}: onTriggerEvent:  ${uiComponent.message}")
            return
        }

        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove() // can throw exception if empty
            state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("${CUSTOM_TAG}: removeHeadMessage: Nothing to remove from DialogQueue")
        }
    }

    private fun onRetryNetwork() {
        getHome()
    }


    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }

    fun openBarcodeScanner(navigateToDetail: (String, Boolean) -> Unit) {
        var skuRegex: String
        viewModelScope.launch {
            val jsonSettings = appDataStoreManager.readValue(DataStoreKeys.CUSTOMER_CONFIG)
            val customerConfig = jsonSettings?.takeIf { it.isNotEmpty() }?.let {
                try {
                    Json.decodeFromString(CustomerConfig.serializer(), it)
                } catch (e: Exception) {
                    // Log the error and handle it gracefully
                    null
                }
            }
            skuRegex = customerConfig?.skuRegex ?: "^\\d{9}$"
            appDataStoreManager.openActivity(skuRegex) { result ->
                navigateToDetail(result, true)
            }
        }
    }

    private fun getDeviceData() {
        viewModelScope.launch {
            val username = appDataStoreManager.readValue(DataStoreKeys.EMAIL) ?: ""
            val jsonSalesMan = appDataStoreManager.readValue(DataStoreKeys.SALES_MAN)
            val user = jsonSalesMan?.let { Json.decodeFromString(SalesMan.serializer(), it) }
            val name = user?.username ?: ""

            appDataStoreManager.fetchDeviceData(this) { result ->
                println("DeviceData, uuid: ${result.uuid}")
                println("DeviceData, username: $username")
                println("DeviceData, name: $name")
                println("DeviceData, version: ${result.version}")
                println("DeviceData, deviceType: ${result.deviceType}")
                println("DeviceData, modelName: ${result.modelName}")
                println("DeviceData, lastInteractionTime: ${Clock.System.now().toEpochMilliseconds()}")
//

                // Collecting the flow emitted by execute function
                launch {
                    deviceDataInteractor.execute(
                        result.uuid ?: "",
                        username,
                        name ?: "",
                        result.version ?: "",
                        result.deviceType ?: "",
                        result.modelName ?: "",
                        Clock.System.now().toEpochMilliseconds()
                    ).collect { dataState ->
                        // Handle the emitted states here
                        when (dataState) {
                            is DataState.Loading -> {
                                println("DeviceData, loading state: ${dataState.progressBarState}")
                                // Handle loading state
                            }

                            is DataState.NetworkStatus -> {
                                println("DeviceData, network status: ${dataState.networkState}")
                                // Handle network status
                            }

                            is DataState.Data -> {
                                println("DeviceData, data received")
                                // Handle successful data
                            }

                            else -> {
                                println("DeviceData, unknown state")
                                // Handle other cases
                            }
                        }
                    }
                }

                println("DeviceData, after execute")
            }
        }
    }

}