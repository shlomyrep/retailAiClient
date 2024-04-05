package presentation.ui.main.scanner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import common.NavigateToScanner

import org.jetbrains.compose.resources.ExperimentalResourceApi
import presentation.component.DefaultScreenUI
import presentation.ui.main.scanner.view_model.ScannerEvent
import presentation.ui.main.scanner.view_model.ScannerState

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun ScannerScreen(
    navigateToDetail: (String) -> Unit = {},
    state: ScannerState,
    events: (ScannerEvent) -> Unit = {},
    navigateToSearch: (String?, Int?) -> Unit = { _, _ -> },
) {

//     Obtain the LocalContext
//    val context = LocalContext.current


    // This LaunchedEffect will execute when the ScannerScreen enters composition
    LaunchedEffect(key1 = Unit) {
        NavigateToScanner().navigate()
    }

    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(ScannerEvent.OnRemoveHeadFromQueue) },
        progressBarState = state.progressBarState,
        networkState = state.networkState,
        onTryAgain = { events(ScannerEvent.OnRetryNetwork) }
    ) {
        // Your UI here
    }



}

