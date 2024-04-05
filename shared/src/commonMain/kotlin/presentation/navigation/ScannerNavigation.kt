package presentation.navigation

sealed class ScannerNavigation(
    val route: String,
    val objectName: String = "",
    val objectPath: String = "",

) {
   data object Search : ScannerNavigation(
        route = "Search",
        objectName = "category_id",
        objectPath = "/{category_id}",

    )

   data object Scanner : ScannerNavigation(route = "Home")
   data object Detail : ScannerNavigation(route = "Detail", objectName = "id", objectPath = "/{id}")

}

