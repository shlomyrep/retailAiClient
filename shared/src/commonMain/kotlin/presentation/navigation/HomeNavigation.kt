package presentation.navigation

sealed class HomeNavigation(
    val route: String,
    val objectName: String = "",
    val objectPath: String = "",
    val objectName2: String = "",
    val objectPath2: String = "",
    val objectName3: String = "",
    val objectPath3: String = ""
) {
    data object Search : HomeNavigation(
        route = "Search",
        objectName = "categoryId",
        objectPath = "/{categoryId}",
        objectName2 = "supplierId",
        objectPath2 = "/{supplierId}",
        objectName3 = "sort",
        objectPath3 = "/{sort}"
    )

    data object Home : HomeNavigation(route = "Home")

    data object Notification : HomeNavigation(route = "Notification")

    data object Categories : HomeNavigation(route = "Categories")
    data object Suppliers : HomeNavigation(route = "Suppliers")

    data object Settings : HomeNavigation(route = "Setting")

    data object Detail : HomeNavigation(route = "Detail", objectName = "id", objectPath = "/{id}")
}

