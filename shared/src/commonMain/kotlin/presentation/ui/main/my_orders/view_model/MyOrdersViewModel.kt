package presentation.ui.main.my_orders.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.constants.CUSTOM_TAG
import business.constants.DataStoreKeys
import business.core.AppDataStore
import business.core.DataState
import business.core.NetworkState
import business.core.Queue
import business.core.UIComponent
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.SizeSelectable
import business.datasource.network.main.responses.getCustomizationSteps
import business.domain.main.Content
import business.domain.main.CustomerConfig
import business.domain.main.EmailData
import business.domain.main.Line
import business.domain.main.Order
import business.domain.main.OrderProduct
import business.domain.main.Quote
import business.domain.main.SalesMan
import business.interactors.main.GetOrdersInteractor
import business.interactors.main.QuoteInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.compose.resources.ExperimentalResourceApi

class MyOrdersViewModel(
    private val getOrdersInteractor: GetOrdersInteractor,
    private val quoteInteractor: QuoteInteractor,
    private val appDataStoreManager: AppDataStore
) : ViewModel() {

    val state: MutableState<MyOrdersState> = mutableStateOf(MyOrdersState())
    private val _orderIdSaved = MutableStateFlow(false)
    val orderIdSaved: StateFlow<Boolean> = _orderIdSaved


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
                onSendQuote(
                    event.orderType,
                    event.order,
                    event.shouldSplitPdf
                )
            }

            is MyOrdersEvent.OnEditOrder -> {
                onEditOrder(event.orderId)
            }
        }
    }

    private fun onUpdatePdfUrl(pdfUrl: String, suppliersPdfs: Map<String, String>?) {
        state.value = state.value.copy(orderPdf = pdfUrl)
        state.value = state.value.copy(suppliersPdfs = suppliersPdfs)
    }

    private fun onEditOrder(orderId: String) {
        viewModelScope.launch {
            appDataStoreManager.setValue(
                DataStoreKeys.EDIT_ODER_ID,
                orderId
            )
            _orderIdSaved.value = true
        }
    }

    fun resetOrderIdSaved() {
        _orderIdSaved.value = false
    }

    private fun onSendQuote(orderType: Int, order: Order, shouldSplitPdf: Boolean) {
        var erpCodeID = ""
        viewModelScope.launch {
            val jsonSalesMan = appDataStoreManager.readValue(DataStoreKeys.SALES_MAN)
            val user = jsonSalesMan?.let { Json.decodeFromString(SalesMan.serializer(), it) }
            erpCodeID = user?.erpID ?: ""
        }
        val quote = Quote(
            order.code,
            orderType,
            order.customerId,
            erpCodeID,
            setEmailDataObject(order.firstName, order.lastName, order, shouldSplitPdf)
        )
        sendQuote(quote)
    }

    init {
        getOrders()
        getCustomerIdRegex()
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
                    dataState.data?.let { data ->
                        if (data.orderPdf.isNotEmpty() || data.suppliersPdfs?.isNotEmpty() == true) {
                            onUpdatePdfUrl(data.orderPdf, data.suppliersPdfs)
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
        order: Order,
        shouldSplitPdf: Boolean
    ): EmailData {
        val emailData = EmailData()
        emailData.split_by_supplier = shouldSplitPdf
        val products = mutableListOf<OrderProduct>()
        emailData.title = "$firstName $lastName"
        emailData.date = order.createdAt
        emailData.header_image = "https://www.retailai.shop/images/tuboul.png"

        order.products.forEach { product ->
            // Create a new OrderProduct for each product to avoid reusing the same instance
            val orderProduct = OrderProduct()
            val contentList = mutableListOf<Content>()
            val imagesList = mutableSetOf<String>()
            val shortDesc = product.shortDescription
            val roomName = product.roomName
            val supplier = product.supplier.companyName
            val sku = product.getCalculatedSku()

            // Add the main image if available
            product.images.firstOrNull()?.let {
                imagesList.add(it.url)
            }
            // Add the sketch image if available
            product.images.firstOrNull { it.is_sketch }?.let {
                imagesList.add(it.url)
            }

            val customizationSteps = getCustomizationSteps(product = product, originalProduct = product)
            val lineList = mutableListOf<Line>()
            lineList.add(Line(text = product.title))
            lineList.add(Line(text = product.name))
            lineList.add(Line(text = sku))

            customizationSteps.forEach { step ->
                step.selector?.selected?.let { selected ->
                    when (selected) {
                        is ColorSelectable -> {
                            val text = "${step.selector.selectionDesc}: ${selected.name}"
                            if (selected.name?.isNotEmpty() == true) {
                                val colorAsset = if (selected.hex?.isNotEmpty() == true) selected.hex else selected.img
                                val assetList = arrayListOf<String>()
                                if (colorAsset != null) {
                                    assetList.add(colorAsset)
                                }
                                lineList.add(Line(assets = assetList, text = text))
                            }
                        }

                        is ProductSelectable -> {
                            contentList.add(Content(lineList.toMutableList(), selected.sku))
                            lineList.clear()
                            if (selected.name.isNotEmpty()) {
                                val assetList = arrayListOf<String>()
                                if (selected.isActive) {
                                    selected.images.firstOrNull()?.let { img ->
                                        assetList.add(img.url)
                                    }
                                }
                                lineList.add(
                                    Line(
                                        assets = assetList,
                                        text = selected.name,
                                        longDescription = selected.longDescription
                                    )
                                )
                            }
                        }

                        is SizeSelectable -> {
                            if (selected.size?.isNotEmpty() == true) {
                                val assetList = arrayListOf<String>()
                                val mainProductSize =
                                    (product.selections.firstOrNull { s -> s.selector?.selected is SizeSelectable })?.selector?.selected as? SizeSelectable
                                var ld = ""
                                if (selected == mainProductSize) {
                                    ld = product.longDescription
                                }
                                lineList.add(Line(assets = assetList, text = selected.size, longDescription = ld))
                            }
                        }
                    }
                }
            }

            contentList.add(Content(lineList.toMutableList(), sku))
            orderProduct.content = contentList
            orderProduct.main_images = imagesList.toMutableList()
            orderProduct.shortDescription = shortDesc
            orderProduct.roomName = roomName
            orderProduct.supplier = supplier ?: ""
            products.add(orderProduct)
        }
        emailData.products = products
        return emailData
    }


    private fun getCustomerIdRegex() {
        viewModelScope.launch {
            val jsonSettings = appDataStoreManager.readValue(DataStoreKeys.CUSTOMER_CONFIG)
            val customerConfig = jsonSettings?.takeIf { it.isNotEmpty() }?.let {
                try {
                    Json.decodeFromString(CustomerConfig.serializer(), it)
                } catch (e: Exception) {
                    // Log the error and return null if deserialization fails
                    null
                }
            }
            val customerIdRegex = customerConfig?.customerIdRegex ?: "^(|[45]\\d{7})$"
            state.value = state.value.copy(customerIdRegex = customerIdRegex)
        }
    }

    fun openPdf(url: String) {
        appDataStoreManager.openPdfUrl(url)
    }
}