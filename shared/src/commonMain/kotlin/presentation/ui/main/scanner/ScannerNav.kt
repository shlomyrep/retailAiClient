package presentation.ui.main.scanner

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.koinInject
import presentation.navigation.ScannerNavigation
import presentation.ui.main.detail.DetailNav
import presentation.ui.main.scanner.view_model.ScannerViewModel

@Composable
fun ScannerNav(logout: () -> Unit) {
    val navigator = rememberNavigator()
    NavHost(
        navigator = navigator,
        initialRoute = ScannerNavigation.Scanner.route,
    ) {
        scene(route = ScannerNavigation.Scanner.route) {
            val viewModel: ScannerViewModel = koinInject()
            ScannerScreen(
                state = viewModel.state.value,
                events = viewModel::onTriggerEvent,
                navigateToDetail = {
                    navigator.popBackStack()
                    navigator.navigate(ScannerNavigation.Detail.route.plus("/$it"))
                }) { categoryId, sort ->
                navigator.navigate(
                    ScannerNavigation.Search.route.plus("/${categoryId}").plus("/${sort}")
                )
            }
        }

        scene(route = ScannerNavigation.Detail.route.plus(ScannerNavigation.Detail.objectPath)) { backStackEntry ->
            val id: String? = backStackEntry.path<String>(ScannerNavigation.Detail.objectName)
            id?.let {
                DetailNav(it) {
                    navigator.popBackStack()
                }
            }
        }
    }
}
