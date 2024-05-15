package presentation.ui.main.detail.view_model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import business.constants.CUSTOM_TAG
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.core.UIComponentState
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.PriceType
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.Selection
import business.datasource.network.main.responses.SizeSelectable
import business.domain.main.BatchItem
import business.interactors.main.AddBasketInteractor
import business.interactors.main.LikeInteractor
import business.interactors.main.ProductInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.vat_included
import shoping_by_kmp.shared.generated.resources.vat_not_included

class DetailViewModel(
    private val productInteractor: ProductInteractor,
    private val addBasketInteractor: AddBasketInteractor,
    private val likeInteractor: LikeInteractor,
    private val appDataStoreManager: AppDataStore
) : ViewModel() {

    val state: MutableState<DetailState> = mutableStateOf(DetailState())
    val inventoryStatusText = mutableStateOf("")
    val heldInventoryText = mutableStateOf("")
    val inventoryStatusColor = mutableStateOf(Color.Black)
    val inventoryClickable = mutableStateOf(false)
    val inventoryUnderLine = mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    var formattedQuantity = ""
    var formattedFreeQuantity = ""


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
                addBasket(id = event.product)
            }

            is DetailEvent.OnUpdateSelectedImage -> {
                onUpdateSelectedImage(event.value)
            }

            is DetailEvent.MakeSelection -> {
                makeSelection(event.selection, event.selectionId)
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

            is DetailEvent.OnUpdateImageOptionDialog -> {
                onUpdateImageOptionDialog(event.value)
            }

            is DetailEvent.OnUpdatePermissionDialog -> {
                onUpdatePermissionDialog(event.value)
            }

            is DetailEvent.OnAddImage -> {
                event.imageBitmap?.let { addImage(it) }
            }
        }
    }

    private fun addImage(imageBitmap: ImageBitmap) {
        uploadImage(imageBitmap, state.value.product.getCalculatedSku(), state.value.product.id)
    }

    private fun onUpdateImageOptionDialog(value: UIComponentState) {
        state.value = state.value.copy(imageOptionDialog = value)
    }

    private fun onUpdatePermissionDialog(value: UIComponentState) {
        state.value = state.value.copy(permissionDialog = value)
    }

    private fun makeSelection(selection: Selection, selectedId: String) {
//        state.value = sizeSelectable?.let { state.value.copy(product = product) }!!
        val updatedSelections = state.value.product.selections.map { sel ->
            if (sel == selection) {
                // Update the selected item in the selection
                sel.copy(
                    selector = sel.selector?.copy(
                        selected = sel.selectionList?.firstOrNull { it._id == selectedId }
                    )
                )
            } else {
                sel
            }
        }

        // Create a new product with the updated selections
        val updatedProduct = state.value.product.copy(selections = updatedSelections)

        // Update the state with the new product
        state.value = state.value.copy(product = updatedProduct)


//        getProductInventory(product.supplier.supplierId ?: "", product.getCalculatedSku())
    }

//    private fun selectColor(colorSelectable: String, product: ProductSelectable) {
//        state.value = colorSelectable?.let { state.value.copy(colorSelectable = it) }!!
//        getProductInventory(product.supplier.supplierId ?: "", product.getCalculatedSku())
//    }

//    private fun selectProduct(productSelectable: String, product: ProductSelectable) {
//        state.value = productSelectable?.let { state.value.copy(colorSelectable = it) }!!
//    }

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
        addBasketInteractor.execute(id = id, 1).onEach { dataState ->
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

                        product.supplier.supplierId?.let { supplierId ->
                            getProductInventory(
                                supplierId,
                                product.getCalculatedSku()
                            )
                        }
                    }
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun uploadImage(bitmap: ImageBitmap, sku: String, id: String) {
        productInteractor.uploadImage(bitmap = bitmap, sku = sku, productId = id)
            .onEach { dataState ->
                when (dataState) {
                    is DataState.NetworkStatus -> {
                        onTriggerEvent(DetailEvent.OnUpdateNetworkState(dataState.networkState))
                    }

                    is DataState.Response -> {
                        onTriggerEvent(DetailEvent.Error(dataState.uiComponent))
                    }

                    is DataState.Data -> {
                        dataState.data?.let { addImageResult ->
                            val currentImages = state.value.galleryImages.toMutableList()
                            addImageResult.product.images.forEach { image ->
                                currentImages.add(image.url)
                            }
                            state.value = state.value.copy(galleryImages = currentImages)
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
        inventoryStatusText.value = DetailTexts().inventoryUpdate
        productInteractor.getProductInventory(supplierId = supplierId, sku = sku)
            .onEach { dataState ->
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
                            0 -> DetailTexts().no
                            1 -> DetailTexts().yes
                            else -> {
                                DetailTexts().no
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
                inventoryStatusText.value = DetailTexts().inventoryNotAvailable
                inventoryStatusColor.value = Color.Black
                inventoryClickable.value = false
                inventoryUnderLine.value = false
            }

            batchItemList.size == 1 -> {
                val quantity = batchItemList[0].quantity
                val freeQuantity = batchItemList[0].freeQuantity

                formattedQuantity = formatToOneDecimalPlace(quantity)
                formattedFreeQuantity = formatToOneDecimalPlace(freeQuantity)

                inventoryStatusText.value = DetailTexts().singleInventoryResult
                inventoryStatusColor.value = Color.Black
                inventoryClickable.value = false
                inventoryUnderLine.value = false
            }

            else -> {
                inventoryStatusText.value = DetailTexts().inventoryList
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

    private fun getPrice(product: ProductSelectable, useUpgradePrice: Boolean = false): Int {
        return when (product.priceType) {
            PriceType.SINGLE_PRICE.toString() -> {
                if (useUpgradePrice) product.upgradePrice?.toInt() ?: 0
                else product.basePrice?.toInt() ?: 0
            }

            PriceType.SIZES_PRICE.toString() -> {
                product.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }
                    ?.selector?.selected?.let { selected ->
                        if (useUpgradePrice) selected.upgradePrice?.toInt() ?: 0
                        else selected.basePrice?.toInt() ?: 0
                    } ?: 0
            }

            PriceType.COLOR_SIZES_PRICE.toString(), PriceType.COLOR_PRICE.toString() -> {
                val selectedColor = product.selections
                    .firstOrNull { it.selector?.selectionType == ColorSelectable.type }
                    ?.selector?.selected

                product.selections.firstOrNull { it.selector?.selectionType == SizeSelectable.type }
                    ?.selector?.selected?.let { selected ->
                        val colorsMap = (selected as? SizeSelectable)?.colors
                        colorsMap?.let {
                            if (selectedColor != null) {
                                if (useUpgradePrice) colorsMap[selectedColor._id]?.upgradePrice?.toInt()
                                    ?: 0
                                else colorsMap[selectedColor._id]?.basePrice?.toInt() ?: 0
                            } else 0
                        }
                    } ?: 0
            }

            else -> 0
        }
    }

    @Composable
    @OptIn(ExperimentalResourceApi::class)
    fun getProductPrice(product: ProductSelectable): String {
        var price = getPrice(product)
        product.selections.filter { it.selector?.selectionType == ProductSelectable.type }
            .forEach { selection ->
                val subProduct = selection.selector?.selected as? ProductSelectable
                subProduct?.let {
                    price += getPrice(it, product.priceIncludeSubProducts)
                }
            }
        if (product.supplier.shouldAddVatToPrice == true) {
            price = (price * 1.17).toInt()  // Apply VAT
        }
        val vatText =
            if (product.supplier.shouldAddVatToPrice == true) stringResource(Res.string.vat_included) else stringResource(
                Res.string.vat_not_included
            )
        return "$price ₪ $vatText"
    }

    fun openPdf(url: String) {
        appDataStoreManager.openPdfUrl(url)
    }
}

