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

    LaunchedEffect(key1 = Unit) {
        // val context = LocalContext.current
        // NavigateToScanner()
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

