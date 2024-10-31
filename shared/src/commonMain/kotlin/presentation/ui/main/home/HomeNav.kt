package presentation.ui.main.home

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject
import presentation.navigation.HomeNavigation
import presentation.ui.main.categories.CategoriesNav
import presentation.ui.main.detail.DetailNav
import presentation.ui.main.home.view_model.HomeViewModel
import presentation.ui.main.notifications.NotificationsScreen
import presentation.ui.main.notifications.view_model.NotificationsViewModel
import presentation.ui.main.search.SearchNav
import presentation.ui.main.settings.SettingsScreen
import presentation.ui.main.settings.view_model.SettingsViewModel

@Composable
fun HomeNav(
    logout: () -> Unit,
    navigateToDetailId: String? = null,
    navigateToDetailIsSKU: Boolean? = null
) {
    val navigator = rememberNavigator()
    println("Shlomy $navigateToDetailId $navigateToDetailIsSKU")
    // Automatically navigate to Detail if parameters are provided
    if (navigateToDetailId != null && navigateToDetailIsSKU != null) {
        // Trigger navigation to Detail with the given parameters
        navigator.navigate(
            HomeNavigation.Detail.route.plus("/$navigateToDetailId")
                .plus("/$navigateToDetailIsSKU"),
            NavOptions(launchSingleTop = true)
        )
    }


    NavHost(
        navigator = navigator,
        initialRoute = HomeNavigation.Home.route,
    ) {
        scene(route = HomeNavigation.Home.route) {
            val viewModel: HomeViewModel = koinInject()
            HomeScreen(
                state = viewModel.state.value,
                getBarcode = {
                    viewModel.openBarcodeScanner { productSku, isSKU: Boolean ->
                        navigator.navigate(
                            HomeNavigation.Detail.route.plus("/$productSku").plus("/$isSKU")
                        )
                    }
                },
                events = viewModel::onTriggerEvent,
                navigateToNotifications = {
                    navigator.navigate(HomeNavigation.Notification.route)
                },
                navigateToCategories = {
                    navigator.navigate(HomeNavigation.Categories.route)
                },
                navigateToSetting = {
                    navigator.navigate(HomeNavigation.Settings.route)
                },
                navigateToDetail = { id: String, isSKU: Boolean ->
                    navigator.popBackStack()
                    navigator.navigate(HomeNavigation.Detail.route.plus("/$id").plus("/$isSKU"))
                })
            { categoryId, sort ->
                navigator.navigate(
                    HomeNavigation.Search.route.plus("/${categoryId}").plus("/${sort}")
                )
            }
        }

        scene(route = HomeNavigation.Settings.route) {
            val viewModel: SettingsViewModel = koinInject()
            SettingsScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                logout = logout,
                popup = {
                    navigator.popBackStack()
                },
            )
        }

        scene(
            route = HomeNavigation.Categories.route
        ) {
            CategoriesNav {
                navigator.popBackStack()
            }
        }
        scene(
            route = HomeNavigation.Search.route
                .plus(HomeNavigation.Search.objectPath)
                .plus(HomeNavigation.Search.objectPath2)
        ) { backStackEntry ->
            val categoryId: String? = backStackEntry.path<String>(HomeNavigation.Search.objectName)
            val sort: Int? = backStackEntry.path<Int>(HomeNavigation.Search.objectName2)
            SearchNav(categoryId = categoryId, sort = sort) {
                navigator.popBackStack()
            }
        }
        scene(route = HomeNavigation.Detail.route.plus("/{id}/{isSKU}")) { backStackEntry ->
            val id: String? = backStackEntry.path<String>("id")
            val isSKU: Boolean = backStackEntry.path<Boolean>("isSKU") ?: false
            id?.let {
                DetailNav(it, isSKU) {
                    navigator.popBackStack()
                }
            }
        }

        scene(route = HomeNavigation.Notification.route) {
            val viewModel: NotificationsViewModel = koinInject()
            NotificationsScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                popup = {
                    navigator.popBackStack()
                },
            )
        }

    }
}
