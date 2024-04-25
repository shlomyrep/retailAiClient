package business.domain.main

import business.datasource.network.main.responses.ProductSelectable


data class Basket(
    val id: String,
    val product: ProductSelectable,
    val category: Category,
    val title: String,
    val description: String,
    val image: String,
    val price: Int,
    val count: Int,
){
    fun getPrice() = "$ $price"
}