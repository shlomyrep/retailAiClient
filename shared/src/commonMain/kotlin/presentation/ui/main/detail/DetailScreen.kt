package presentation.ui.main.detail

import ExpandingText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.Selection
import business.datasource.network.main.responses.SizeSelectable
import business.domain.main.BatchItem
import business.domain.main.Comment
import kotlinx.coroutines.delay
import presentation.component.CircleButton
import presentation.component.CircleImage
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_16dp
import presentation.component.Spacer_32dp
import presentation.component.Spacer_4dp
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.component.rememberCustomImagePainter
import presentation.theme.BorderColor
import presentation.theme.orange_400
import presentation.ui.main.detail.view_model.DetailEvent
import presentation.ui.main.detail.view_model.DetailState
import presentation.ui.main.detail.view_model.DetailViewModel
import presentation.util.convertDate


@Composable
fun DetailScreen(
    popup: () -> Unit,
    navigateToMoreComment: (String) -> Unit,
    state: DetailState,
    events: (DetailEvent) -> Unit,
    viewModel: DetailViewModel
) {
    val showDialog = viewModel.showDialog

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
                        val batches = viewModel.state.value.productInventoryBatch.batchesList
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
                                InventoryStatusText(
                                    viewModel = viewModel,
                                    onDialogRequest = viewModel::show
                                )
                                if (showDialog) {
                                    BatchListDialog(
                                        batches = state.productInventoryBatch.batchesList,
                                        onDismiss = viewModel::dismiss
                                    )
                                }
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
                        Selections(state.product, events)

                        Spacer_16dp()

                        Text(
                            "פרטי מוצר",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer_8dp()

                        val productDescription = getProductDescription(state.product, viewModel.heldInventoryText.value)
                        ExpandingText(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            text = productDescription,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)

                        ) {}

                        Spacer_32dp()
                    }
                }
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                    BuyButtonBox(
                        state.product
                    ) {
                        events(DetailEvent.AddBasket(state.product))
                    }
                }
            }
        }
    }
}

@Composable
fun ColorGrid(selection: Selection, events: (DetailEvent) -> Unit, product: ProductSelectable) {
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
                                events(DetailEvent.SelectColor(it1, product))
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

@Composable
fun ProductGrid(selection: Selection, events: (DetailEvent) -> Unit) {
    if (selection.selector?.selected is ProductSelectable) {
        selection.selector?.selectionDesc?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(selection.selectionList ?: listOf()) { selectable ->
                val product = selectable as ProductSelectable
                ProductCard(product, selection, events)
            }
        }
    }
}

@Composable
fun ProductCard(product: ProductSelectable, selection: Selection, events: (DetailEvent) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .clickable {
                product._id?.let { productId ->
                    selection.selector?.selected = product
//                    events(DetailEvent.SelectProduct(productId))
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Image handling
            val imageUrl = product.images.firstOrNull()?.url ?: ""
            Image(
                painter = rememberCustomImagePainter(imageUrl),
                contentDescription = "Product Image",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


fun String.toColorInt(): Int {
    try {
        if (this[0] == '#') {
            var color = substring(1).toLong(16)
            if (length == 7) {
                color = color or 0x00000000ff000000L
            } else if (length != 9) {
                return Color.White.toArgb()
            }
            return color.toInt()
        }
    } catch (e: Exception) {
        return Color.White.toArgb()
    }
    return Color.White.toArgb()
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
fun BuyButtonBox(product: ProductSelectable, onClick: () -> Unit) {
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
                Text("sku", style = MaterialTheme.typography.titleMedium)
                Text(product.getCalculatedSku(), style = MaterialTheme.typography.titleLarge)
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
fun Selections(product: ProductSelectable, events: (DetailEvent) -> Unit) {
    product.selections.forEach {
        SizeGrid(it, events, product)
        ColorGrid(it, events, product)
        ProductGrid(it, events)
    }
}

@Composable
fun SizeGrid(selection: Selection, events: (DetailEvent) -> Unit, product: ProductSelectable) {


    if (selection.selector?.selected is SizeSelectable) {
        Spacer_8dp()
        // Calculate the number of rows needed
        val numRows = (selection.selectionList?.size ?: 0 + 2) / 3

        Column(modifier = Modifier.padding(8.dp)) {
            for (row in 0 until numRows) {
                Row {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        if (index < selection.selectionList?.size ?: 0) {
                            val size = selection.selectionList?.get(index) as SizeSelectable
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable {
                                        size._id?.let { it1 ->
                                            selection.selector.selected = size
                                            // Trigger your event here
                                            events(DetailEvent.SelectSize(it1, product))
                                        }
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = if ((selection.selector.selected as SizeSelectable)._id == size._id) MaterialTheme.colorScheme.primary else BorderColor,
                                        shape = MaterialTheme.shapes.small
                                    )
                            ) {
                                Text(
                                    text = size.size ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if ((selection.selector.selected as SizeSelectable)._id == size._id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f)) // Empty space for missing items
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

@Composable
fun InventoryStatusText(viewModel: DetailViewModel, onDialogRequest: () -> Unit) {
    val inventoryStatus = viewModel.inventoryStatusText.value
    val inventoryColor = viewModel.inventoryStatusColor.value
    val isClickable = viewModel.inventoryClickable.value
    val isUnderline = viewModel.inventoryUnderLine.value
    val isLoading = viewModel.isLoading.value

    var dots by remember { mutableStateOf("") }
    LaunchedEffect(isLoading) {
        while (isLoading) {
            dots = when (dots) {
                "    " -> ".   "
                ".   " -> "..  "
                "..  " -> "... "
                "...  " -> ".... "
                else -> "    "
            }
            delay(200) // Pause for half a second between updates
        }
        dots = "    "
    }

    val displayText = if (isLoading) " מעדכן מלאי $dots " else inventoryStatus

    val text = if (isUnderline) {
        buildAnnotatedString {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(displayText)
            }
        }
    } else {
        AnnotatedString(displayText)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(color = inventoryColor),
        modifier = Modifier
            .clickable(enabled = isClickable, onClick = onDialogRequest)
            .padding(16.dp)
    )
}

@Composable
fun BatchListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "אצווה",
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "מלאי",
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "זמין",
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BatchItemRow(batchItem: BatchItem, index: Int) {
    val backgroundColor = if (index % 2 == 0) Color.LightGray else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = batchItem.batch,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,

            )
        Text(
            text = "${batchItem.quantity}",
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "${batchItem.freeQuantity}",
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BatchListDialog(batches: List<BatchItem>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.6f)
        ) {
            LazyColumn {
                // Define the sticky header
                stickyHeader {
                    BatchListHeader()
                }
                // List items
                itemsIndexed(batches) { index, batch ->
                    BatchItemRow(batch, index)
                }
            }
        }
    }
}

@Composable
fun getProductDescription(product: ProductSelectable, heldInventory: String): AnnotatedString {
    return buildAnnotatedString {
        // Add supplier information if available
        if (product.supplier.companyName?.isNotEmpty() == true) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("ספק: ")
            }
            append("${product.supplier.companyName}\n")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("מוחזק מלאי: ")
        }
        append("$heldInventory\n")
        product.selections.forEach { selection ->
            when (val selected = selection.selector?.selected) {
                is ColorSelectable -> {
                    selected.name?.let {
                        if (it.isNotEmpty()) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("גוון: ")
                            }
                            append("$it\n")
                        }
                    }
                }

                is ProductSelectable -> {
                    if (selected.name.isNotEmpty()) {
                        val selectionDesc = selection.selector.selectionDesc ?: ""
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$selectionDesc: ")
                        }
                        append("${selected.name}\n")
                    }
                }

                is SizeSelectable -> {
                    selected.size?.let {
                        if (it.isNotEmpty() && it != "0") {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${selection.selector.selectionDesc}: ")
                            }
                            append("$it ס״מ\n")
                        }
                    }
                }

                null -> {}
            }
        }
    }
}





