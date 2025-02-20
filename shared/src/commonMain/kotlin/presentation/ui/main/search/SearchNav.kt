package presentation.ui.main.search


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import business.domain.main.Category
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject
import presentation.navigation.SearchNavigation
import presentation.ui.main.detail.DetailNav
import presentation.ui.main.search.view_model.SearchEvent
import presentation.ui.main.search.view_model.SearchViewModel

@Composable
fun SearchNav(categoryId: String?, supplierId: String?, sort: Int?, popUp: () -> Unit) {
    val navigator = rememberNavigator()
    NavHost(
        navigator = navigator,
        initialRoute = SearchNavigation.Search.route,
    ) {
        scene(route = SearchNavigation.Search.route) {
            val viewModel: SearchViewModel = koinInject()
            LaunchedEffect(categoryId, supplierId, sort) {
                // Sanitize categoryId
                val categories = categoryId
                    ?.takeIf { it.isNotEmpty() && it != "null" }
                    ?.let { listOf(Category(id = it)) }
                    ?: emptyList()

                // Sanitize supplierId
                val suppliers = supplierId
                    ?.takeIf { it.isNotEmpty() && it != "null" }
                    ?.let { it }
                    ?: ""

                // Update sort if it's not -1
                sort?.takeIf { it != -1 }?.let {
                    viewModel.onTriggerEvent(SearchEvent.OnUpdateSelectedSort(it))
                }

                // Trigger Search event with both categories and suppliers
                viewModel.onTriggerEvent(
                    SearchEvent.Search(categories = categories, supplier = suppliers)
                )
            }


            SearchScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToDetailScreen = {
                    navigator.popBackStack()
                    navigator.navigate(SearchNavigation.Detail.route.plus("/$it"))
                },
                popUp = { popUp() }
            )
        }
        scene(route = SearchNavigation.Detail.route.plus(SearchNavigation.Detail.objectPath)) { backStackEntry ->
            val id: String? = backStackEntry.path<String>(SearchNavigation.Detail.objectName)
            id?.let {
                DetailNav(it, false) {
                    navigator.popBackStack()
                }
            }
        }
    }
}



