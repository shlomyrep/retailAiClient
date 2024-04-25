package presentation.ui.main.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.Selection
import business.datasource.network.main.responses.SizeSelectable
import business.domain.main.Basket
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_16dp
import presentation.component.Spacer_4dp
import presentation.component.noRippleClickable
import presentation.component.rememberCustomImagePainter
import presentation.theme.BorderColor
import presentation.ui.main.cart.view_model.CartEvent
import presentation.ui.main.cart.view_model.CartState


@Composable
fun CartScreen(
    state: CartState,
    events: (CartEvent) -> Unit,
    navigateToDetail: (String) -> Unit,
    navigateToCheckout: () -> Unit,
) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DefaultScreenUI(
            queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(CartEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState,
            networkState = state.networkState,
            onTryAgain = { events(CartEvent.OnRetryNetwork) }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().align(Alignment.Center)
                        .padding(bottom = 100.dp) // Adjust this value as needed to accommodate the floating button area
                ) {
                    items(state.baskets) {
                        CartBox(
                            it,
                            addMoreProduct = {
                                events(CartEvent.AddProduct(it.product))
                            },
                            navigateToDetail = navigateToDetail
                        ) {
                            events(CartEvent.DeleteFromBasket(it.product.id))
                        }
                    }
                }

                if (state.baskets.isNotEmpty()) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                        ProceedButtonBox(
                            state.totalCost
                        ) {
                            navigateToCheckout()
                        }
                    }
                }


                if (state.baskets.isEmpty()) {
                    Box(
                        modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Basket is empty!",
                            style = MaterialTheme.typography.labelLarge,
                            color = BorderColor,
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun ProceedButtonBox(totalCost: String, onClick: () -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Cost", style = MaterialTheme.typography.titleMedium)
                Text(totalCost, style = MaterialTheme.typography.titleLarge)
            }

            Spacer_16dp()

            DefaultButton(
                modifier = Modifier.fillMaxWidth().height(DEFAULT__BUTTON_SIZE),
                text = "Proceed to Checkout"
            ) {
                onClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBox(
    basket: Basket,
    navigateToDetail: (String) -> Unit,
    addMoreProduct: () -> Unit,
    deleteFromBasket: () -> Unit
) {
    var show by remember { mutableStateOf(true) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                deleteFromBasket()
                show = false
                true
            } else false
        }
    )

    AnimatedVisibility(
        show, exit = fadeOut(spring())
    ) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = Modifier,
            backgroundContent = {
                DismissBackground(dismissState)
            },
            content = {
                DismissCartContent(
                    basket,
                    addMoreProduct = addMoreProduct,
                    navigateToDetail = navigateToDetail
                )
            })
    }
}

@Composable
fun DismissCartContent(
    basket: Basket,
    addMoreProduct: () -> Unit,
    navigateToDetail: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp)
                    .weight(.3f)
                    .clip(MaterialTheme.shapes.small)
                    .noRippleClickable {
                        navigateToDetail(basket.product.id)
                    }
            ) {
                Image(
                    painter = rememberCustomImagePainter(basket.image),
                    null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer_16dp()

            Column(modifier = Modifier.weight(.4f)) {
                Text(
                    basket.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer_4dp()
                Text(
                    basket.category.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer_4dp()
                Text(
                    constructSelections(basket.product.selections),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxHeight()
                    .weight(.3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.size(25.dp),
                    shape = MaterialTheme.shapes.small,
                    onClick = {},
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("-")
                    }
                }
                Spacer_4dp()
                Text(basket.count.toString())
                Spacer_4dp()
                Card(
                    modifier = Modifier.size(25.dp),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        addMoreProduct()
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "+",
                        )
                    }
                }

            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}

fun constructSelections(selections: List<Selection>): String {
    val selectionDescriptions = selections.mapNotNull { selection ->
        when (val selected = selection.selector?.selected) {
            is ColorSelectable -> selected.name
            is ProductSelectable -> selected.name
            is SizeSelectable -> if (selected.size == "0") "" else selected.size
            else -> null
        }
    }
    return selectionDescriptions.joinToString("\n")
}


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DismissBackground(dismissState: SwipeToDismissBoxState) {
        val color = MaterialTheme.colorScheme.primary.copy(alpha = .2f)
        val direction = dismissState.dismissDirection

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (direction == SwipeToDismissBoxValue.EndToStart) Icon(
                Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "delete"
            )
            Spacer(modifier = Modifier)
        }
    }



