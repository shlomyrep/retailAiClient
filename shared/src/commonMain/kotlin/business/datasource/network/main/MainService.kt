package business.datasource.network.main

import androidx.compose.ui.graphics.ImageBitmap
import business.datasource.network.common.JRNothing
import business.datasource.network.common.MainGenericResponse
import business.datasource.network.main.responses.AddImageResult
import business.datasource.network.main.responses.AddressDTO
import business.datasource.network.main.responses.BasketDTO
import business.datasource.network.main.responses.CommentDTO
import business.datasource.network.main.responses.HeldInventoryBatchDTO
import business.datasource.network.main.responses.HomeDTO
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.ProfileDTO
import business.datasource.network.main.responses.SearchDTO
import business.datasource.network.main.responses.SearchFilterDTO
import business.datasource.network.main.responses.WishlistDTO
import business.datasource.network.main.responses.Order
import business.datasource.network.main.responses.OrderResponse
import business.domain.main.SalesMan

interface MainService {
    companion object {
        const val SEARCH_FILTER = "search/filter"
        const val SEARCH = "search"
        const val BASKET = "cart"
        const val BUY = "cart/buy"
        const val BASKET_ADD = "cart/add"
        const val BASKET_DELETE = "cart/delete"
        const val HOME = "home"
        const val ORDERS = "cart/all"
        const val PRODUCT = "search/product"
        const val PRODUCT_SKU = "search/sku"
        const val LIKE = "like"
        const val PROFILE = "user/profile"
        const val COMMENT = "comment"
        const val WISHLIST = "wishlist"
        const val ADDRESS = "address"
        const val PRODUCT_INVENTORY = "inventory"
    }

    suspend fun getOrders(
        token: String,
    ): MainGenericResponse<List<Order>>

    suspend fun buyProduct(
        token: String,
        salesMan: SalesMan,
        customerFirstName: String,
        customerLastName: String,
        customerId: String
    ): MainGenericResponse<JRNothing>

    suspend fun getAddresses(
        token: String,
    ): MainGenericResponse<List<AddressDTO>>

    suspend fun addAddress(
        token: String,
        address: String,
        city: String,
        country: String,
        state: String,
        zipCode: String,
    ): MainGenericResponse<JRNothing>

    suspend fun getComments(
        token: String,
        id: Int,
    ): MainGenericResponse<List<CommentDTO>>

    suspend fun addComment(
        token: String,
        productId: Int,
        rate: Double,
        comment: String,
    ): MainGenericResponse<JRNothing>

    suspend fun search(
        token: String,
        minPrice: Int?,
        maxPrice: Int?,
        sort: Int?,
        categoriesId: String?,
        page: Int,
    ): MainGenericResponse<SearchDTO>

    suspend fun getSearchFilter(
        token: String,
    ): MainGenericResponse<SearchFilterDTO>

    suspend fun getProfile(token: String): MainGenericResponse<ProfileDTO>
    suspend fun updateProfile(
        token: String,
        name: String,
        age: String,
        image: ByteArray?,
    ): MainGenericResponse<Boolean>

    suspend fun basket(token: String, salesMan: SalesMan): MainGenericResponse<List<BasketDTO>>
    suspend fun basketAdd(
        token: String,
        salesMan: SalesMan,
        id: ProductSelectable,
        count: Int
    ): MainGenericResponse<JRNothing?>

    suspend fun basketDelete(
        token: String,
        id: String,
        sales: SalesMan
    ): MainGenericResponse<JRNothing?>

    suspend fun home(token: String): MainGenericResponse<HomeDTO>
    suspend fun productBySku(token: String, sku: String): MainGenericResponse<ProductSelectable>
    suspend fun product(token: String, id: String): MainGenericResponse<ProductSelectable>

    suspend fun sendSpecProducts(token: String, order: Order): MainGenericResponse<OrderResponse>

    suspend fun productInventory(
        token: String,
        supplierId: String,
        sku: String
    ): HeldInventoryBatchDTO

    suspend fun uploadImage(
        token: String,
        bitmap: ImageBitmap,
        sku: String,
        productId: String
    ): AddImageResult

    suspend fun like(token: String, id: String): MainGenericResponse<JRNothing?>
    suspend fun wishlist(
        token: String,
        categoryId: Int?,
        page: Int
    ): MainGenericResponse<WishlistDTO>
}