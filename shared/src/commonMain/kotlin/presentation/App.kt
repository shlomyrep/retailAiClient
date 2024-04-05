package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.fetch.NetworkFetcher
import common.Context
import common.NavigateToScanner
import di.appModule
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import presentation.component.noRippleClickable
import presentation.navigation.AppNavigation
import presentation.theme.AppTheme
import presentation.theme.Gray
import presentation.theme.ProgressBarColor
import presentation.theme.White
import presentation.ui.main.MainNav
import presentation.ui.splash.SplashNav
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.barcode_scanner
import shoping_by_kmp.shared.generated.resources.bell

@OptIn(ExperimentalCoilApi::class, ExperimentalResourceApi::class)
@Composable
internal fun App(context: Context) {

    KoinApplication(application = {
        modules(appModule(context))
    }) {
        PreComposeApp {


            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .components {
                        add(NetworkFetcher.Factory())
                    }
                    .build()
            }

            AppTheme {
                val navigator = rememberNavigator()
                val viewModel: SharedViewModel = koinInject()

                LaunchedEffect(key1 = viewModel.tokenManager.state.value.isTokenAvailable) {
                    if (!viewModel.tokenManager.state.value.isTokenAvailable) {
                        navigator.popBackStack()
                        navigator.navigate(AppNavigation.Splash.route)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navigator = navigator,
                        initialRoute = AppNavigation.Splash.route,
                    ) {
                        scene(route = AppNavigation.Splash.route) {
                            SplashNav(navigateToMain = {
                                navigator.popBackStack()
                                navigator.navigate(AppNavigation.Main.route)
                            })
                        }
                        scene(route = AppNavigation.Main.route) {
                            MainNav {
                                navigator.popBackStack()
                                navigator.navigate(AppNavigation.Splash.route)
                            }
                        }
                    }
                    // This Box is used for the QR code button
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 90.dp, start = 16.dp)
                            .size(56.dp)
                            .background(
                                color = Gray,
                                shape = CircleShape
                            )
                            .clickable {
                                NavigateToScanner(context).navigate()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR Code",
                            tint = ProgressBarColor
                        )
                    }
                }

            }
        }
    }
}




