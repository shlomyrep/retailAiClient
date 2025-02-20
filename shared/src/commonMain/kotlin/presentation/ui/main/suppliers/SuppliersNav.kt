package presentation.ui.main.suppliers

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject
import presentation.navigation.SuppliersNavigation
import presentation.ui.main.search.SearchNav
import presentation.ui.main.suppliers.view_model.SuppliersViewModel

@Composable
fun SuppliersNav(popup: () -> Unit) {

    val navigator = rememberNavigator()
    NavHost(
        navigator = navigator,
        initialRoute = SuppliersNavigation.Suppliers.route,
    ) {

        scene(route = SuppliersNavigation.Suppliers.route) {
            val viewModel: SuppliersViewModel = koinInject()
            SuppliersScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                popup = popup,
            ) { supplierId ->
                navigator.navigate(
                    SuppliersNavigation.Search.route.plus("/${supplierId}")
                )
            }
        }

        scene(
            route = SuppliersNavigation.Search.route
                .plus(SuppliersNavigation.Search.objectPath)
        ) { backStackEntry ->
            val categoryId: String? = backStackEntry.path<String>(SuppliersNavigation.Search.objectName)
            SearchNav(categoryId = categoryId, sort = null) {
                navigator.popBackStack()
            }
        }
    }
}

