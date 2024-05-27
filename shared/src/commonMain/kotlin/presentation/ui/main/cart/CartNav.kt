package presentation.ui.main.cart

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject
import presentation.navigation.CartNavigation
import presentation.navigation.HomeNavigation
import presentation.ui.main.address.AddressScreen
import presentation.ui.main.address.view_model.AddressViewModel
import presentation.ui.main.cart.view_model.CartViewModel
import presentation.ui.main.checkout.CheckoutScreen
import presentation.ui.main.checkout.view_model.CheckoutViewModel
import presentation.ui.main.detail.DetailNav

@Composable
fun CartNav() {
    val navigator = rememberNavigator()
    NavHost(
        navigator = navigator,
        initialRoute = CartNavigation.Cart.route,
    ) {
        scene(route = CartNavigation.Cart.route) {
            val viewModel: CartViewModel = koinInject()
            CartScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToDetail = { id: String, isSKU: Boolean ->
                    navigator.popBackStack()
                    navigator.navigate(HomeNavigation.Detail.route.plus("/$id").plus("/$isSKU"))
                }, navigateToCheckout = {
                    navigator.navigate(CartNavigation.Checkout.route)
                })
        }
        scene(route = CartNavigation.Checkout.route) {
            val viewModel: CheckoutViewModel = koinInject()
            CheckoutScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToAddress = {
                    navigator.navigate(CartNavigation.Address.route)
                },
                popup = { navigator.popBackStack() },
            )
        }
        scene(route = CartNavigation.Address.route) {
            val viewModel: AddressViewModel = koinInject()
            AddressScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                popup = { navigator.popBackStack() },
            )
        }

        scene(route = CartNavigation.Detail.route.plus("/{id}/{isSKU}")) { backStackEntry ->
            val id: String? = backStackEntry.path<String>("id")
            val isSKU: Boolean = backStackEntry.path<Boolean>("isSKU") ?: false
            id?.let {
                DetailNav(it, isSKU) {
                    navigator.popBackStack()
                }
            }
        }
    }
}
