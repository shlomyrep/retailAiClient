package presentation.navigation

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.cart
import shoping_by_kmp.shared.generated.resources.cart_border
import shoping_by_kmp.shared.generated.resources.heart2
import shoping_by_kmp.shared.generated.resources.heart_border2
import shoping_by_kmp.shared.generated.resources.home
import shoping_by_kmp.shared.generated.resources.home_border
import shoping_by_kmp.shared.generated.resources.profile
import shoping_by_kmp.shared.generated.resources.profile_border

@OptIn(ExperimentalResourceApi::class)
sealed class MainNavigation (
    val route: String,
    val title: String,
    val selectedIcon: DrawableResource,
    val unSelectedIcon: DrawableResource,
) {

   data object Home : MainNavigation(
        route = "Home", title = "Home",
        selectedIcon = Res.drawable.home,
        unSelectedIcon = Res.drawable.home_border
    )

   data object Wishlist : MainNavigation(
        route = "Wishlist", title = "Wishlist",
        selectedIcon = Res.drawable.heart2,
        unSelectedIcon = Res.drawable.heart_border2
    )

   data object Cart : MainNavigation(
        route = "Cart", title = "Cart",
        selectedIcon = Res.drawable.cart,
        unSelectedIcon = Res.drawable.cart_border
    )

   data object Profile : MainNavigation(
        route = "Profile", title = "Profile",
        selectedIcon = Res.drawable.profile,
        unSelectedIcon = Res.drawable.profile_border
    )
}

