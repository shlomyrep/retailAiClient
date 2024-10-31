package presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import common.ChangeStatusBarColors
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import presentation.navigation.HomeNavigation
import presentation.navigation.MainNavigation
import presentation.theme.DefaultNavigationBarItemTheme
import presentation.ui.main.cart.CartNav
import presentation.ui.main.home.HomeNav
import presentation.ui.main.home.view_model.HomeViewModel
import presentation.ui.main.profile.ProfileNav
import presentation.ui.main.wishlist.WishlistNav

@Composable
fun MainNav(logout: () -> Unit) {

    val navigator = rememberNavigator()

    ChangeStatusBarColors(Color.White)
    Scaffold(bottomBar = {
        BottomNavigationUI(navigator)
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navigator = navigator,
                initialRoute = MainNavigation.Home.route,
            ) {
                scene(route = MainNavigation.Home.route) {
                    println("Navigating to Home without parameters")

                    HomeNav(
                        logout = logout,
                        navigateToDetailId = null,
                        navigateToDetailIsSKU = null
                    )
                }
                scene(route = "${MainNavigation.Home.route}/{productSku}/{isSKU}") { backStackEntry ->
                    val productSku: String? = backStackEntry.path<String>("productSku")
                    val isSKU: Boolean? = backStackEntry.path<Boolean>("isSKU")
                    println("Navigating to Home with parameters: productSku=$productSku, isSKU=$isSKU")

                    HomeNav(
                        logout = logout,
                        navigateToDetailId = productSku,
                        navigateToDetailIsSKU = if (productSku != null) isSKU else null
                    )
                }
                scene(route = MainNavigation.Wishlist.route) {
                    WishlistNav()
                }
                scene(route = MainNavigation.Cart.route) {
                    CartNav()
                }
                scene(route = MainNavigation.Profile.route) {
                    ProfileNav(logout = logout)
                }
            }
        }

    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomNavigationUI(navigator: Navigator) {
    val viewModel: HomeViewModel = koinInject()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            // Add the items, including a new Scanner item in the middle
            val items = listOf(
                MainNavigation.Home,
                MainNavigation.Wishlist,
                MainNavigation.Scanner,  // New Scanner button added here
                MainNavigation.Cart,
                MainNavigation.Profile,
            )

            // Iterate through items and add them to the NavigationBar
            items.forEach { item ->
                NavigationBarItem(
                    label = { Text(text = item.title) },
                    colors = DefaultNavigationBarItemTheme(),
                    selected = item.route == currentRoute(navigator),
                    icon = {
                        Icon(
                            painter = painterResource(item.selectedIcon),
                            contentDescription = item.title
                        )
                    },
                    onClick = {
                        // We will define the action later; right now, we just add the button.
                        if (item is MainNavigation.Scanner) {
                            // Scanner action will be defined here later.
                            viewModel.openBarcodeScanner { productSku, isSKU ->
                                // Navigate to Home with parameters
                                println("Shlomy $productSku -- --- ")
                                val route = "${MainNavigation.Home.route}/$productSku/$isSKU"

                                navigator.navigate(
                                    route,
                                    NavOptions(launchSingleTop = true)
                                )
                            }
                        } else {
                            navigator.navigate(
                                item.route,
                                NavOptions(launchSingleTop = true)
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun currentRoute(navigator: Navigator): String? {
    return navigator.currentEntry.collectAsState(null).value?.route?.route

}