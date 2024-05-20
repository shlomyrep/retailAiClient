package business.datasource.network.main

import androidx.compose.ui.graphics.ImageBitmap
import business.constants.BASE_URL
import business.datasource.network.common.JRNothing
import business.datasource.network.common.MainGenericResponse
import business.datasource.network.main.responses.AddImageResult
import business.datasource.network.main.responses.AddressDTO
import business.datasource.network.main.responses.AddressRequestDTO
import business.datasource.network.main.responses.BasketAddRequestDTO
import business.datasource.network.main.responses.BasketDTO
import business.datasource.network.main.responses.BasketDeleteRequestDTO
import business.datasource.network.main.responses.BuyRequestDTO
import business.datasource.network.main.responses.CommentDTO
import business.datasource.network.main.responses.CommentRequestDTO
import business.datasource.network.main.responses.HeldInventoryBatchDTO
import business.datasource.network.main.responses.HomeDTO
import business.datasource.network.main.responses.OrderDTO
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.ProfileDTO
import business.datasource.network.main.responses.SearchDTO
import business.datasource.network.main.responses.SearchFilterDTO
import business.datasource.network.main.responses.WishlistDTO
import business.domain.main.OrderResponse
import business.domain.main.Quote
import business.domain.main.SalesMan
import common.toBytes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.util.InternalAPI
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully

class MainServiceImpl(
    private val httpClient: HttpClient
) : MainService {
    override suspend fun getOrders(
        token: String, salesMan: SalesMan
    ): MainGenericResponse<List<OrderDTO>> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.ORDERS
                parameter("sales", salesMan.erpID)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun buyProduct(
        token: String,
        salesMan: SalesMan,
        customerFirstName: String,
        customerLastName: String,
        customerId: String
    ): MainGenericResponse<JRNothing> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.BUY
            }
            contentType(ContentType.Application.Json)
            setBody(
                BuyRequestDTO(
                    salesMan = salesMan,
                    firstName = customerFirstName,
                    lastName = customerLastName,
                    customerId = customerId
                )
            )
        }.body()
    }

    override suspend fun getAddresses(token: String): MainGenericResponse<List<AddressDTO>> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.ADDRESS
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun addAddress(
        token: String,
        address: String,
        city: String,
        country: String,
        state: String,
        zipCode: String
    ): MainGenericResponse<JRNothing> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.ADDRESS
            }
            contentType(ContentType.Application.Json)
            setBody(
                AddressRequestDTO(
                    address = address,
                    city = city,
                    country = country,
                    state = state,
                    zipCode = zipCode
                )
            )
        }.body()
    }

    override suspend fun getComments(
        token: String,
        id: Int
    ): MainGenericResponse<List<CommentDTO>> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.COMMENT
                encodedPath += "/$id"
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun addComment(
        token: String,
        productId: Int,
        rate: Double,
        comment: String
    ): MainGenericResponse<JRNothing> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.COMMENT
            }
            contentType(ContentType.Application.Json)
            setBody(CommentRequestDTO(comment = comment, rate = rate, productId = productId))
        }.body()
    }

    override suspend fun search(
        token: String,
        minPrice: Int?,
        maxPrice: Int?,
        sort: Int?,
        categoriesId: String?,
        page: Int
    ): MainGenericResponse<SearchDTO> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.SEARCH

                parameter("categories_id", categoriesId)
                parameter("sort", sort)
                parameter("page", page)
                parameter("min_price", minPrice)
                parameter("max_price", maxPrice)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getSearchFilter(
        token: String,
    ): MainGenericResponse<SearchFilterDTO> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.SEARCH_FILTER
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getProfile(token: String): MainGenericResponse<ProfileDTO> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.PROFILE
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    @OptIn(InternalAPI::class)
    override suspend fun updateProfile(
        token: String,
        name: String,
        age: String,
        image: ByteArray?
    ): MainGenericResponse<Boolean> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.PROFILE
            }
            // contentType(ContentType.MultiPart.FormData)
            /*body = formData {
                append("name", name)
                if (image != null) {
                    append("image", image)
                }
                append("age", age)
            }*/

            body = MultiPartFormDataContent(
                formData {
                    append("name", name)
                    append("age", age)
                    this.append(FormPart("image", "image.jpg"))
                    this.appendInput(
                        key = "image",
                        headers = Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=image.jpg"
                            )
                        },
                    ) {
                        buildPacket {
                            if (image != null) {
                                writeFully(image)
                            }
                        }
                    }
                }
            )

        }.body()
    }

    override suspend fun basket(
        token: String,
        salesMan: SalesMan
    ): MainGenericResponse<List<BasketDTO>> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.BASKET
                parameters.append("sales", salesMan.erpID)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun basketAdd(
        token: String,
        salesMan: SalesMan,
        productSelectable: ProductSelectable,
        cartItemId:String
    ): MainGenericResponse<JRNothing?> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.BASKET_ADD
            }
            contentType(ContentType.Application.Json)
            setBody(
                BasketAddRequestDTO(
                    product = productSelectable,
                    selections = productSelectable.selections,
                    user = salesMan,
                    cartItemId = cartItemId
                )
            )
        }.body()
    }

    override suspend fun basketDelete(
        token: String, id: String,
        sales: SalesMan
    ): MainGenericResponse<JRNothing?> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.BASKET_DELETE
            }
            contentType(ContentType.Application.Json)
            setBody(BasketDeleteRequestDTO(product = id, salesMan = sales))
        }.body()
    }

    override suspend fun home(token: String): MainGenericResponse<HomeDTO> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.HOME
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun product(
        token: String,
        id: String
    ): MainGenericResponse<ProductSelectable> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.PRODUCT
                encodedPath += "/$id"
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun productBySku(
        token: String,
        sku: String
    ): MainGenericResponse<ProductSelectable> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.PRODUCT_SKU
                encodedPath += "/$sku"
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun productInventory(
        token: String,
        supplierId: String,
        sku: String
    ): HeldInventoryBatchDTO {
        return httpClient.get {
            headers {
                append(HttpHeaders.Authorization, token)
            }
            url {
                takeFrom(BASE_URL + MainService.PRODUCT_INVENTORY)
                parameters.append("supplierId", supplierId)
                parameters.append("sku", sku)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun sendQuote(
        token: String,
        quote: Quote
    ): MainGenericResponse<OrderResponse> {
        return httpClient.post {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL + "product/mail")
            }
            contentType(ContentType.Application.Json)
            setBody(quote)
        }.body()
    }


    @OptIn(InternalAPI::class)
    override suspend fun uploadImage(
        token: String,
        bitmap: ImageBitmap,
        sku: String,
        productId: String
    ): AddImageResult {
        val imageUrl = "$BASE_URL/product/picture/$productId?sku=$sku"
        return httpClient.post {
            url(imageUrl)
            headers {
                append(HttpHeaders.Authorization, token)
            }
            body = MultiPartFormDataContent(
                formData {
                    append(
                        "picture",
                        bitmap.toBytes(),
                        Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"picture\"; filename=\"image.jpg\""
                            )
                            append(HttpHeaders.ContentType, "image/jpeg")
                        }
                    )
                }
            )
        }.body<AddImageResult>()
    }

    override suspend fun like(token: String, id: String): MainGenericResponse<JRNothing?> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.PRODUCT
                encodedPath += "/$id/"
                encodedPath += MainService.LIKE
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun wishlist(
        token: String,
        categoryId: Int?,
        page: Int
    ): MainGenericResponse<WishlistDTO> {
        return httpClient.get {
            url {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                takeFrom(BASE_URL)
                encodedPath += MainService.WISHLIST
                parameter("category_id", categoryId)
                parameter("page", page)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }
}