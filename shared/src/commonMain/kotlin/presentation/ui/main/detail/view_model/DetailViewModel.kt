package presentation.ui.main.detail.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import business.constants.CUSTOM_TAG
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.datasource.network.main.responses.ProductSelectable
import business.domain.main.BatchItem
import business.interactors.main.AddBasketInteractor
import business.interactors.main.LikeInteractor
import business.interactors.main.ProductInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class DetailViewModel(
    private val productInteractor: ProductInteractor,
    private val addBasketInteractor: AddBasketInteractor,
    private val likeInteractor: LikeInteractor,
) : ViewModel() {


    val state: MutableState<DetailState> = mutableStateOf(DetailState())
    val inventoryStatusText = mutableStateOf("מעדכן מלאי")
    val heldInventoryText = mutableStateOf("")

    val inventoryStatusColor = mutableStateOf(Color.Black)
    val inventoryClickable = mutableStateOf(false)
    val inventoryUnderLine = mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    val isLoading = mutableStateOf(false)


    fun show() {
        showDialog = true
    }

    fun dismiss() {
        showDialog = false
    }


    fun onTriggerEvent(event: DetailEvent) {
        when (event) {

            is DetailEvent.Like -> {
                likeProduct(id = event.id)
            }

            is DetailEvent.AddBasket -> {
                addBasket(id = event.id)
            }

            is DetailEvent.OnUpdateSelectedImage -> {
                onUpdateSelectedImage(event.value)
            }

            is DetailEvent.SelectSize -> {
                selectSize(event.sizeSelectableId, event.product)
            }

            is DetailEvent.SelectColor -> {
                selectColor(event.colorSelectableId, event.product)
            }

            is DetailEvent.GetProduct -> {
                getProduct(event.id)
            }

            is DetailEvent.GetProductInventory -> {
                getProductInventory(event.supplierId, event.sku)
            }

            is DetailEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }

            is DetailEvent.Error -> {
                appendToMessageQueue(event.uiComponent)
            }

            is DetailEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is DetailEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
    }

    private fun selectSize(sizeSelectable: String, product: ProductSelectable) {
        state.value = sizeSelectable?.let { state.value.copy(sizeSelectable = it) }!!
        getProductInventory(product.supplier.supplierId ?: "", product.getCalculatedSku())
    }

    private fun selectColor(colorSelectable: String, product: ProductSelectable) {
        state.value = colorSelectable?.let { state.value.copy(colorSelectable = it) }!!
        getProductInventory(product.supplier.supplierId ?: "", product.getCalculatedSku())
    }


    private fun onUpdateSelectedImage(value: String) {
        state.value = state.value.copy(selectedImage = value)
    }


    private fun likeProduct(id: String) {
        likeInteractor.execute(id = id)
            .onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onTriggerEvent(DetailEvent.Error(dataState.uiComponent))
                    }

                    is DataState.Data -> {
                        dataState.data?.let {
                            if (it) updateLike()
                        }
                    }

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
    }


    private fun addBasket(id: ProductSelectable) {
        addBasketInteractor.execute(id = id, 1)
            .onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        onTriggerEvent(DetailEvent.Error(dataState.uiComponent))
                    }

                    is DataState.Data -> {}

                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
    }


    private fun updateLike() {
        state.value =
            state.value.copy(product = state.value.product.copy(isLike = !state.value.product.isLike))
    }


    private fun getProduct(id: String) {
        productInteractor.execute(id = id).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(DetailEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    onTriggerEvent(DetailEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    dataState.data?.let { product ->
                        state.value = state.value.copy(product = product)
                        state.value =
                            state.value.copy(selectedImage = product.gallery.firstOrNull() ?: "")

                        product.supplier.supplierId?.let { supplierId -> getProductInventory(supplierId, product.sku) }
//                        getProductInventory("6358ea2f19992d304ce3821a", "117011212")
                    }
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getProductInventory(supplierId: String, sku: String) {
        isLoading.value = true
        inventoryStatusColor.value = Color.Black
        inventoryClickable.value = false
        inventoryUnderLine.value = false
        inventoryStatusText.value = "מעדכן מלאי"
        productInteractor.getProductInventory(supplierId = supplierId, sku = sku).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
//                    onTriggerEvent(DetailEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    handleInventoryResponse(null)
                }

                is DataState.Data -> {
                    dataState.data?.let { batchItems ->
                        handleInventoryResponse(batchItems.batchesList)
                        state.value = state.value.copy(productInventoryBatch = batchItems)

                    }
                    heldInventoryText.value = when (dataState.data?.heldInventory) {
                        0 -> "מוחזק מלאי: לא"
                        1 -> "מוחזק מלאי: כן"
                        else -> {
                            "מוחזק מלאי: לא"
                        }
                    }
                }

                is DataState.Loading -> {
//                    state.value = state.value.copy(progressBarState = dataState.progressBarState)
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
        getProduct(state.value.product.id)
    }


    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }

    private fun handleInventoryResponse(batchItemList: List<BatchItem>?) {
        when {
            batchItemList.isNullOrEmpty() -> {
                inventoryStatusText.value = "לא קיים מלאי עבור פריט זה"
                inventoryStatusColor.value = Color.Black
                inventoryClickable.value = false
                inventoryUnderLine.value = false
            }

            batchItemList.size == 1 -> {
                val quantity = batchItemList[0].quantity
                val freeQuantity = batchItemList[0].freeQuantity

                val formattedQuantity = formatToOneDecimalPlace(quantity)
                val formattedFreeQuantity = formatToOneDecimalPlace(freeQuantity)

                inventoryStatusText.value = "מלאי: $formattedQuantity, זמין: $formattedFreeQuantity"
                inventoryStatusColor.value = Color.Black
                inventoryClickable.value = false
                inventoryUnderLine.value = false
            }

            else -> {
                inventoryStatusText.value = "רשימת מלאי"
                inventoryStatusColor.value = Color.Blue
                inventoryClickable.value = true
                inventoryUnderLine.value = true
            }
        }
        isLoading.value = false
    }

    private fun formatToOneDecimalPlace(number: Double): String {
        // Check if the number has a decimal part
        return if (number % 1 == 0.0) {
            // If no decimal part, convert directly to Int and then to String
            number.toInt().toString()
        } else {
            // If there is a decimal part, round to one decimal place manually
            val roundedNumber = kotlin.math.round(number * 10) / 10
            // Convert to String, this will have one digit after the decimal point due to the rounding above
            roundedNumber.toString()
        }
    }


}