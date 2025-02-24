package presentation.navigation

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.barcode_scanner
import retailai.shared.generated.resources.cart
import retailai.shared.generated.resources.cart_border
import retailai.shared.generated.resources.heart2
import retailai.shared.generated.resources.heart_border2
import retailai.shared.generated.resources.home
import retailai.shared.generated.resources.home_border
import retailai.shared.generated.resources.profile
import retailai.shared.generated.resources.profile_border

@OptIn(ExperimentalResourceApi::class)
sealed class MainNavigation(
    val route: String,
    val title: String,
    val selectedIcon: DrawableResource,
    val unSelectedIcon: DrawableResource
) {
    data object Home : MainNavigation(
        route = "Home", title = "בית",
        selectedIcon = Res.drawable.home,
        unSelectedIcon = Res.drawable.home_border
    )

    // New Scanner Item
    data object Scanner : MainNavigation(
        route = "Scanner", title = "סריקה",
        selectedIcon = Res.drawable.barcode_scanner,
        unSelectedIcon = Res.drawable.barcode_scanner
    )

    data object Wishlist : MainNavigation(
        route = "Wishlist", title = "מועדפים",
        selectedIcon = Res.drawable.heart2,
        unSelectedIcon = Res.drawable.heart_border2
    )

    data object Cart : MainNavigation(
        route = "Cart", title = "סל\nמוצרים",
        selectedIcon = Res.drawable.cart,
        unSelectedIcon = Res.drawable.cart_border,
    )

   data object Profile : MainNavigation(
        route = "Profile", title = "אזור אישי",
        selectedIcon = Res.drawable.profile,
        unSelectedIcon = Res.drawable.profile_border
    )
}

