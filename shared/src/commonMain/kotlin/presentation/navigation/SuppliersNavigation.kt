package presentation.navigation

sealed class SuppliersNavigation(
    val route: String,
    val objectName: String = "",
    val objectPath: String = "",
) {
   data object Suppliers : SuppliersNavigation(route = "Suppliers")


   data object Search : SuppliersNavigation(
        route = "Search",
        objectName = "Supplier_id",
        objectPath = "/{Supplier_id}",
    )

}

