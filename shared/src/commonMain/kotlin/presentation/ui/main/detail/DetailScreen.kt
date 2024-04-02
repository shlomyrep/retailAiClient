package presentation.ui.main.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.Selection
import business.datasource.network.main.responses.SizeSelectable
import business.domain.main.Comment
import business.domain.main.Product
import presentation.component.CircleButton
import presentation.component.CircleImage
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.ExpandingText
import presentation.component.Spacer_16dp
import presentation.component.Spacer_32dp
import presentation.component.Spacer_4dp
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.component.rememberCustomImagePainter
import presentation.theme.BackgroundContent
import presentation.theme.BorderColor
import presentation.theme.orange_400
import presentation.ui.main.detail.view_model.DetailEvent
import presentation.ui.main.detail.view_model.DetailState
import presentation.util.convertDate


@Composable
fun DetailScreen(
    popup: () -> Unit,
    navigateToMoreComment: (String) -> Unit,
    state: DetailState,
    events: (DetailEvent) -> Unit
) {


    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(DetailEvent.OnRemoveHeadFromQueue) },
        progressBarState = state.progressBarState,
        networkState = state.networkState,
        onTryAgain = { events(DetailEvent.OnRetryNetwork) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 75.dp)
            ) {

                Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {

                    Image(
                        painter = rememberCustomImagePainter(state.selectedImage),
                        null, modifier = Modifier.fillMaxSize()
                    )

                    Box(modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                        CircleButton(
                            modifier = Modifier.padding(4.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = { popup() })
                    }

                    Box(modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)) {
                        CircleButton(
                            modifier = Modifier.padding(4.dp),
                            imageVector = if (state.product.isLike) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            onClick = {
                                events(DetailEvent.Like(state.product.id))
                            })
                    }

                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter).wrapContentWidth()
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.wrapContentWidth().padding(vertical = 8.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            LazyRow(
                                modifier = Modifier.wrapContentWidth(),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(state.product.gallery) {
                                    ImageSliderBox(it) {
                                        events(DetailEvent.OnUpdateSelectedImage(it))
                                    }
                                }
                            }
                        }
                    }


                }

                Spacer_32dp()

                Column(modifier = Modifier.padding(vertical = 16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            state.product.category.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Star, null, tint = orange_400)
                            Text(
                                state.product.rate.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }


                    Spacer_16dp()


                    Text(
                        state.product.title,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    // a grid that shows the size selection if exists
                    SizeGrid(state.product.selections, events)

                    ColorGrid(state.product.selections, events)

                    Spacer_16dp()

                    Text(
                        "Product Details",
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer_8dp()

                    ExpandingText(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        text = state.product.description,
                        style = MaterialTheme.typography.bodySmall,
                    ) {}


                    Spacer_16dp()

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = BackgroundContent
                    )

                    Spacer_16dp()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Read some comments",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "More",
                            modifier = Modifier
                                .clickable {
                                    navigateToMoreComment(state.product.id)
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                    }

                    Spacer_8dp()

                    if (state.product.comments.isEmpty()) {
                        Text(
                            "No Comments!",
                            style = MaterialTheme.typography.titleLarge,
                            color = BorderColor,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        items(state.product.comments, key = { it.createAt }) {
                            CommentBox(comment = it)
                        }
                    }

                    Spacer_16dp()

                }
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                BuyButtonBox(
                    state.product
                ) {
                    events(DetailEvent.AddBasket(state.product.id))
                }
            }
        }
    }
}

@Composable
fun ColorGrid(selections: List<Selection>, events: (DetailEvent) -> Unit) {
    selections.forEach { selection ->

        if (selection.selector?.selected is ColorSelectable) {
            Spacer_8dp()
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selection.selectionList?.forEach { color ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                color._id?.let { it1 ->
                                    selection.selector.selected = color
                                    events(DetailEvent.SelectColor(it1))
                                }
                            }
                            .padding(8.dp)
                            .border(
                                width = 1.dp,
                                color = if ((selection.selector.selected as ColorSelectable)._id == color._id) MaterialTheme.colorScheme.primary else BorderColor,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        (color as ColorSelectable).hex?.let {
                            ColorBox(it)
                        }
                    }
                }
            }

        }

    }

}

fun String.toColorInt(): Int {
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        } else if (length != 9) {
            throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

@Composable
fun ColorBox(colorHex: String?) {
    colorHex?.let {
        val composeColor = Color(it.toColorInt())
        if (composeColor != Color.Unspecified) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(composeColor)
            )
        }
    }
}

@Composable
fun BuyButtonBox(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Total Price", style = MaterialTheme.typography.titleMedium)
                Text(product.getPrice(), style = MaterialTheme.typography.titleLarge)
            }

            DefaultButton(
                modifier = Modifier.fillMaxWidth(.7f).height(DEFAULT__BUTTON_SIZE),
                text = "Add to Cart"
            ) {
                onClick()
            }
        }
    }
}

@Composable
fun CommentBox(comment: Comment, modifier: Modifier = Modifier.width(300.dp)) {
    Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = modifier.height(160.dp),
            elevation = CardDefaults.cardElevation(8.dp), shape = MaterialTheme.shapes.small
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleImage(
                            image = comment.user.image
                        )
                        Spacer_4dp()
                        Text(comment.user.fetchName(), style = MaterialTheme.typography.titleSmall)
                    }
                    Text(
                        comment.createAt.convertDate(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer_8dp()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        comment.comment,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Star, null, tint = orange_400)
                        Text(comment.rate.toString(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun SizeGrid(selections: List<Selection>, events: (DetailEvent) -> Unit) {
    selections.forEach { selection ->

        if (selection.selector?.selected is SizeSelectable) {
            Spacer_8dp()
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selection.selectionList?.forEach { size ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                size._id?.let { it1 ->
                                    selection.selector.selected = size
                                    events(DetailEvent.SelectSize(it1))
                                }
                            }
                            .padding(8.dp)
                            .border(
                                width = 1.dp,
                                color = if ((selection.selector.selected as SizeSelectable)._id == size._id) MaterialTheme.colorScheme.primary else BorderColor,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        (size as SizeSelectable).size?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if ((selection.selector.selected as SizeSelectable)._id == size._id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ImageSliderBox(it: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(65.dp).clip(MaterialTheme.shapes.small).padding(4.dp)
            .noRippleClickable { onClick() }) {
        Image(
            rememberCustomImagePainter(it),
            null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
