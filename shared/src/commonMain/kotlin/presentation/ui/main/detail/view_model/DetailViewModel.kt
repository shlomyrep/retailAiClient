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
import business.domain.main.BatchItem
import business.domain.main.Product
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
    val inventoryStatusText = mutableStateOf("")
    val inventoryStatusColor = mutableStateOf(Color.Black)
    val inventoryClickable = mutableStateOf(false)
    var showDialog by mutableStateOf(false)
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
                selectSize(event.sizeSelectableId)
            }

            is DetailEvent.SelectColor -> {
                selectColor(event.colorSelectableId)
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

    private fun selectSize(sizeSelectable: String) {
        state.value = sizeSelectable?.let { state.value.copy(sizeSelectable = it) }!!
    }

    private fun selectColor(colorSelectable: String) {
        state.value = colorSelectable?.let { state.value.copy(colorSelectable = it) }!!
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


    private fun addBasket(id: Product) {
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
                    dataState.data?.let {
                        state.value = state.value.copy(product = it)
                        state.value =
                            state.value.copy(selectedImage = it.gallery.firstOrNull() ?: "")

                        getProductInventory("6358ea2f19992d304ce3821a", it.sku)

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
        productInteractor.getProductInventory(supplierId = supplierId, sku = sku).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(DetailEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    handleInventoryResponse(null)
                }

                is DataState.Data -> {
                    dataState.data?.let { batchItems ->
                        handleInventoryResponse(batchItems.batchesList)
                        state.value = state.value.copy(productInventoryBatch = batchItems)
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
            }

            batchItemList.size == 1 -> {
                val quantity = batchItemList[0].quantity
                val freeQuantity = batchItemList[0].freeQuantity

                val roundedOnHand = if (quantity % 1.0 == 0.0) quantity.toInt() else quantity
                val roundedFreeQty = if (freeQuantity % 1.0 == 0.0) freeQuantity.toInt() else freeQuantity

                inventoryStatusText.value = " מלאי : $roundedOnHand, זמין : $roundedFreeQty"
                inventoryStatusColor.value = Color.Black
                inventoryClickable.value = false
            }

            else -> {
                inventoryStatusText.value = "רשימת מלאי"
                inventoryStatusColor.value = Color.Blue
                inventoryClickable.value = true
            }
        }
    }

}