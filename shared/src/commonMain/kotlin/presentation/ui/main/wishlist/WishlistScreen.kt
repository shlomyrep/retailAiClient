package presentation.ui.main.wishlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import business.constants.PAGINATION_PAGE_SIZE
import business.core.ProgressBarState
import presentation.component.CategoryChipsBox
import presentation.component.DefaultScreenUI
import presentation.component.ProductBox
import presentation.component.Spacer_8dp
import presentation.theme.BorderColor
import presentation.ui.main.wishlist.view_model.WishlistEvent
import presentation.ui.main.wishlist.view_model.WishlistState

@Composable
fun WishlistScreen(
    state: WishlistState,
    events: (WishlistEvent) -> Unit,
    navigateToDetail: (String) -> Unit
) {
    // Assuming DefaultScreenUI is a wrapper that also includes a progress indicator or similar
    // Place the LazyRow outside so it's not affected by state changes that DefaultScreenUI responds to.
    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(state.wishlist.categories) { category ->
                CategoryChipsBox(category = category, isSelected = category == state.selectedCategory) {
                    events(WishlistEvent.OnUpdateSelectedCategory(category))
                }
            }
        }

        // Now we include DefaultScreenUI which contains parts of the UI that react to state changes
        DefaultScreenUI(
            queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(WishlistEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState,
            networkState = state.networkState,
            boxAlignment = Alignment.TopStart,
            onTryAgain = { events(WishlistEvent.OnRetryNetwork) }

        ) {
            // Conditional UI elements or the main content grid that changes with state
            if (state.wishlist.products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Wishlist is empty!",
                        style = MaterialTheme.typography.labelLarge,
                        color = BorderColor,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(state.wishlist.products) { index, product ->
                        // Implementation remains the same
                        if ((index + 1) >= (state.page * PAGINATION_PAGE_SIZE) &&
                            state.progressBarState == ProgressBarState.Idle &&
                            state.hasNextPage
                        ) {
                            events(WishlistEvent.GetNextPage)
                        }

                        ProductBox(
                            product = product,
                            modifier = Modifier.fillMaxWidth(.5f),
                            onLikeClick = {
                                events(WishlistEvent.LikeProduct(product.id))
                            }
                        ) {
                            navigateToDetail(product.id)
                        }
                    }
                }
            }
        }
    }
}



