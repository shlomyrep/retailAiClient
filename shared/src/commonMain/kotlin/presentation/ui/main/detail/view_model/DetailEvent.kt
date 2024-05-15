package presentation.ui.main.detail.view_model

import androidx.compose.ui.graphics.ImageBitmap
import business.core.NetworkState
import business.core.UIComponent
import business.core.UIComponentState
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.Selection

sealed class DetailEvent {


    data class Like(val id: String) : DetailEvent()
    data class AddBasket(val product: ProductSelectable) : DetailEvent()

    data class OnUpdateSelectedImage(
        val value: String
    ) : DetailEvent()

    data class GetProduct(
        val id: String,
        val isSku: Boolean
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

    data class MakeSelection(val selectionId: String, val selection: Selection) : DetailEvent()
    data class OnUpdateImageOptionDialog(val value: UIComponentState) : DetailEvent()
    data class OnUpdatePermissionDialog(val value: UIComponentState) : DetailEvent()
    data class OnAddImage(val imageBitmap: ImageBitmap?) : DetailEvent()

}
