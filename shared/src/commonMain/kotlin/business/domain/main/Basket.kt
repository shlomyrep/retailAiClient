package business.domain.main

 
data class Basket(
    val id: String,
    val productId: Product,
    val category: Category,
    val title: String,
    val description: String,
    val image: String,
    val price: Int,
    val count: Int,
){
    fun getPrice() = "$ $price"
}