package presentation.ui.main.detail.view_model

import androidx.compose.ui.graphics.ImageBitmap
import business.core.NetworkState
import business.core.UIComponent
import business.core.UIComponentState
import business.datasource.network.main.responses.ProductSelectable

sealed class DetailEvent {


    data class Like(val id: String) : DetailEvent()
    data class AddBasket(val product: ProductSelectable) : DetailEvent()

    data class OnUpdateSelectedImage(
        val value: String
    ) : DetailEvent()

    data class GetProduct(
        val id: String
    ) : DetailEvent()

    data class GetProductInventory(
        val supplierId: String,
        val sku: String
    ) : DetailEvent()

    data object OnRemoveHeadFromQueue : DetailEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : DetailEvent()

    data object OnRetryNetwork : DetailEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : DetailEvent()

    data class SelectSize(val sizeSelectableId: String, val product: ProductSelectable) : DetailEvent()
    data class SelectColor(val colorSelectableId: String, val product: ProductSelectable) : DetailEvent()
    class SelectProduct(val productSelectableId: String, val product: ProductSelectable) : DetailEvent()

    data class OnUpdateImageOptionDialog(val value: UIComponentState) : DetailEvent()
    data class OnUpdatePermissionDialog(val value: UIComponentState) : DetailEvent()
    data class OnAddImage(val imageBitmap: ImageBitmap?)  : DetailEvent()

}
