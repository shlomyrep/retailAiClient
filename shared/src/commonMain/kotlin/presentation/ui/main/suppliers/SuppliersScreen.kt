package presentation.ui.main.suppliers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import business.domain.main.Supplier
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.ui.main.suppliers.view_model.SuppliersEvent
import presentation.ui.main.suppliers.view_model.SuppliersState
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.suppliers

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SuppliersScreen(
    state: SuppliersState,
    events: (SuppliersEvent) -> Unit,
    popup: () -> Unit,
    navigateToSearch: (String) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DefaultScreenUI(
            queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(SuppliersEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState,
            networkState = state.networkState,
            onTryAgain = { events(SuppliersEvent.OnRetryNetwork) },
            titleToolbar = stringResource(Res.string.suppliers),
            startIconToolbar = Icons.AutoMirrored.Filled.ArrowBack,
            onClickStartIconToolbar = popup
        ) {
            Column(modifier = Modifier.fillMaxSize()) {


                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                ) {
                    items(state.suppliers, key = { it.supplierId }) {
                        SupplierBox(it, modifier = Modifier.weight(1f)) {
                            navigateToSearch(it.supplierId)
                        }
                    }
                }

            }
        }
    }
}


@Composable
private fun SupplierBox(supplier: Supplier, modifier: Modifier, onSupplierClick: () -> Unit) {
    Box(modifier = modifier.padding(8.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.noRippleClickable {
                onSupplierClick()
            }
        ) {
            Box(
                modifier = Modifier.size(75.dp).padding(2.dp).clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(supplier.companyName),
                    null,
                    modifier = Modifier.fillMaxSize().size(65.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer_8dp()
            Text(
                supplier.companyName,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )
        }
    }
}
