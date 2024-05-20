package presentation.ui.main.my_orders.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.CUSTOM_TAG
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.SizeSelectable
import business.datasource.network.main.responses.getCustomizationSteps
import business.domain.main.Content
import business.domain.main.EmailData
import business.domain.main.Line
import business.domain.main.Order
import business.domain.main.OrderProduct
import business.domain.main.Quote
import business.interactors.main.GetOrdersInteractor
import business.interactors.main.QuoteInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.compose.resources.ExperimentalResourceApi
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.model_text
import shoping_by_kmp.shared.generated.resources.sku

class MyOrdersViewModel(
    private val getOrdersInteractor: GetOrdersInteractor,
    private val quoteInteractor: QuoteInteractor
) : ViewModel() {


    val state: MutableState<MyOrdersState> = mutableStateOf(MyOrdersState())


    fun onTriggerEvent(event: MyOrdersEvent) {
        when (event) {


            is MyOrdersEvent.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }

            is MyOrdersEvent.Error -> {
                appendToMessageQueue(event.uiComponent)
            }

            is MyOrdersEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is MyOrdersEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }

            is MyOrdersEvent.OnSendQuote -> {
                onSendQuote(event.orderType, event.customerId, event.erpCodeID, event.firstName, event.lastName, event.order)
            }
        }
    }

    private fun onSendQuote(orderType: Int, customerId: String, erpCodeID: String, firstName: String, lastName: String, order: Order) {
        val quote = Quote(
            orderType,
            customerId,
            erpCodeID,
            setEmailDataObject(firstName, lastName, order)
        )
        sendQuote(quote)
    }

    init {
        getOrders()
    }

    private fun getOrders() {
        getOrdersInteractor.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(MyOrdersEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    onTriggerEvent(MyOrdersEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    state.value = state.value.copy(orders = dataState.data ?: listOf())
                }

                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun sendQuote(quote: Quote) {
        quoteInteractor.execute(quote = quote).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {}
                is DataState.Response -> {
                    onTriggerEvent(MyOrdersEvent.Error(dataState.uiComponent))
                }

                is DataState.Data -> {
                    if (dataState.data?.orderPdf?.isNotEmpty() == true) {
                        state.value = state.value.copy(orderPdf = dataState.data.orderPdf)
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
        getOrders()
    }


    private fun onUpdateNetworkState(networkState: NetworkState) {
        state.value = state.value.copy(networkState = networkState)
    }

    @OptIn(ExperimentalResourceApi::class)
    fun setEmailDataObject(
        firstName: String,
        lastName: String,
        order: Order
    ): EmailData {
        val emailData = EmailData()
        val products = mutableListOf<OrderProduct>()
        val orderProduct = OrderProduct()
        emailData.title ="$firstName $lastName"
        emailData.date = order.createdAt

        // טובול לוגו
        emailData.header_image = "https://www.retailai.shop/images/tuboul.png"

        order.products.forEach { product ->
            val contentList = mutableListOf<Content>()
            val imagesList = mutableSetOf<String>()
            val shortDesc = product.shortDescription
//            val price = if (product.supplier.shouldAddVatToPrice == true) {
//                val originalPriceStr = getProductPrice(product) // This should be a String
//                try {
//                    val originalPrice = BigDecimal(originalPriceStr)
//                    val vatRate = BigDecimal("0.17")
//                    val vatAmount = originalPrice.multiply(vatRate)
//                    originalPrice.add(vatAmount).setScale(2, RoundingMode.HALF_UP).toPlainString()
//                } catch (e: NumberFormatException) {
//                    // Handle the exception if the original price string is not a valid number
//                    "Invalid price"
//                }
//            } else {
//                getProductPrice(specProduct.product)
//            }

//            val notes = product.notes
            val roomName = product.roomName
            val supplier = product.supplier.companyName

//            val imagesSku = KitCatRepo.getImageSku()
//            var sku = getStoreSku(
//                specProduct.product.getCalculatedSku() ?: ""
//            )
            val sku = product.getCalculatedSku()
            // Check if sku exists in imageSku and add distinct images
//            imagesSku[sku]?.let { skuImages ->
//                val newImages = skuImages.map { imageUrl ->
//                    Image(imageUrl, false)
//                }.filterNot { specProduct.product.images.contains(it) }
//                val combinedImages = mutableListOf<Image>().apply {
//                    addAll(newImages)
//                    addAll(specProduct.product.images)
//                }
//                combinedImages.firstOrNull()?.let {
//                    imagesList.add(it.url)
//                }
//            }
            // ADD the Main picture
            product.images.firstOrNull()?.let {
                if (imagesList.isEmpty()) {
                    imagesList.add(it.url)
                }
            }

            // Add the sketch
            product.images.firstOrNull { it.is_sketch }?.let {
                imagesList.add(it.url)
            }

            val customizationSteps = getCustomizationSteps(
                product = product, originalProduct = product
            )

            val lineList = mutableListOf<Line>()
            val productName = product.name
            val productNameLine = Line(
                text = "${Res.string.model_text} $productName",
            )
            lineList.add(productNameLine)
//            Log.i("TEST", "1: $sku")
//            sku = getSupplierToStoreSku(sku)
//            Log.i("TEST", "2: $sku")
            val skuLine = Line(
                text = "${Res.string.sku} $sku",
            )
            lineList.add(skuLine)
//            Log.i("TEST", "3: $sku")
            customizationSteps.map {

                // Prepare a new line
                it.selector?.selected?.let { selected ->
                    when (selected) {
                        is ColorSelectable -> {

                            val selectionDesc = it.selector.selectionDesc
                            val text =
                                "$selectionDesc: ${selected.name}"

                            if (selected.name?.isNotEmpty() == true) {
                                val colorAsset =
                                    if (selected.hex?.isNotEmpty() == true) selected.hex else selected.img
                                val assetList = arrayListOf<String>()
                                if (colorAsset != null) {
                                    assetList.add(colorAsset)
                                }
                                val line = Line(
                                    assets = assetList,
                                    text = text,
                                )
                                println("DIGIT ${line.text}  ${line.assets[0]}")
                                lineList.add(line)
                            }
                        }

                        is ProductSelectable -> {
                            contentList.add(Content(lineList.toMutableList(), selected.sku))
                            lineList.clear()
                            if (selected.name.isNotEmpty()) {
//                                val selectionDesc = when {
//                                    (it.selector.selectionDesc?.contains(
//                                        MainApplication.appContext.getString(
//                                            R.string.surface_to_shower_closet_text
//                                        )
//                                    ) == true) -> {
//                                        MainApplication.appContext.getString(R.string.surface_text_to_replace)
//
//                                    }
//
//                                    (it.selector.selectionDesc?.contains(
//                                        MainApplication.appContext.getString(
//                                            R.string.mirror_text_to_replace
//                                        )
//                                    ) == true) -> {
//                                        MainApplication.appContext.getString(R.string.mirror_replaced_text)
//
//                                    }
//
//                                    (it.selector.selectionDesc?.contains(
//                                        MainApplication.appContext.getString(
//                                            R.string.service_closet_to_shower_closet_text
//                                        )
//                                    ) == true) -> {
//                                        MainApplication.appContext.getString(R.string.service_closet_replaced_text)
//                                    }
//
//                                    else -> {
//                                        it.selector.selectionDesc
//                                    }
//                                }

                                val text = selected.name
                                val assetList = arrayListOf<String>()
                                if (selected.isActive) {
                                    selected.images.firstOrNull()?.let { img ->
                                        assetList.add(img.url)
                                    }
                                }

                                val line = Line(
                                    assets = assetList,
                                    text = text,
                                    longDescription = selected.longDescription
                                )
                                lineList.add(line)
//                                Log.i("DIGIT", selectionDesc ?: "")
                            }
                        }

                        is SizeSelectable -> {
                            if (selected.size?.isNotEmpty() == true) {
//                                var selectionDesc = ""
//                                if (it.selector.selectionDesc?.contains(
//                                        MainApplication.appContext.getString(
//                                            R.string.closet_size_to_shower_closet_text
//                                        )
//                                    ) == true
//                                ) {
//                                    selectionDesc =
//                                        "${
//                                            MainApplication.appContext.getString(
//                                                R.string.shower_closet_model_text
//                                            )
//                                        }${specProduct.product.name} ${
//                                            MainApplication.appContext.getString(
//                                                R.string.size_text
//                                            )
//                                        } ${selected.size}${
//                                            MainApplication.appContext.getString(
//                                                R.string.centimeter_text
//                                            )
//                                        }"
//                                }

//                                if (it.selector.selectionDesc?.contains(
//                                        MainApplication.appContext.getString(
//                                            R.string.mirror_size_text
//                                        )
//                                    ) == true
//                                ) {
//                                    selectionDesc =
//                                        "${it.selector.selectionDesc}:  ${(it.selector.selected as SizeSelectable).size} ${
//                                            MainApplication.appContext.getString(
//                                                R.string.centimeter_text
//                                            )
//                                        } "
//                                }
                                val assetList = arrayListOf<String>()
                                val mainProductSize =
                                    (product.selections.firstOrNull { s -> s.selector?.selected is SizeSelectable })?.selector?.selected as? SizeSelectable
                                var ld = ""
                                if (selected == mainProductSize) {
                                    ld = product.longDescription
                                }
                                val line = Line(
                                    assets = assetList,
                                    text = selected.size,
                                    longDescription = ld
                                )
//                                Log.i("DIGIT", selectionDesc)
                                lineList.add(line)
                            }
                        }
                    }
                }
            }

            contentList.add(Content(lineList.toMutableList(), sku))
            orderProduct.content = contentList
            orderProduct.main_images = imagesList.toMutableList()
            orderProduct.shortDescription = shortDesc
//            orderProduct.quantity = quantity
//            orderProduct.price = price
//            product.notes = notes
            orderProduct.roomName = roomName
            orderProduct.supplier = supplier ?: ""
            products.add(orderProduct)
        }
        emailData.products = products
        return emailData
    }


}