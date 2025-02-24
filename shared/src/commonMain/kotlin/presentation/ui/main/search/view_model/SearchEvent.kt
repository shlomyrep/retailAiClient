package presentation.ui.main.search.view_model

import business.core.NetworkState
import business.core.UIComponent
import business.core.UIComponentState
import business.domain.main.Category

sealed class SearchEvent {


    data class Search(
        val minPrice: Int? = null,
        val maxPrice: Int? = null,
        val categories: List<Category>? = null,
        val supplier: String? = null,
    ) : SearchEvent()

   data object GetNextPage : SearchEvent()

    data class OnUpdateSelectedSort(val value: Int) : SearchEvent()

    data class OnUpdatePriceRange(val value: ClosedFloatingPointRange<Float>) : SearchEvent()

    data class OnUpdateSelectedCategory(val categories: List<Category>) : SearchEvent()

    data class OnUpdateSearchText(val value: String) : SearchEvent()

    data class OnUpdateSortDialogState(val value: UIComponentState) : SearchEvent()

    data class OnUpdateFilterDialogState(val value: UIComponentState) : SearchEvent()

    data class OnUpdateSelectedSupplier(val supplier: String) : SearchEvent()


    data object OnRemoveHeadFromQueue : SearchEvent()

    data class Error(
        val uiComponent: UIComponent
    ) : SearchEvent()

   data object OnRetryNetwork : SearchEvent()
    data class OnUpdateNetworkState(
        val networkState: NetworkState
    ) : SearchEvent()


}
