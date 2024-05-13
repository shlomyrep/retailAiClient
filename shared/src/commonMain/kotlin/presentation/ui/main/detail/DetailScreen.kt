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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import business.core.UIComponentState
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.Selection
import business.datasource.network.main.responses.SizeSelectable
import business.datasource.network.main.responses.getCustomizationSteps
import business.domain.main.BatchItem
import business.domain.main.Comment
import common.PermissionCallback
import common.PermissionStatus
import common.PermissionType
import common.createPermissionsManager
import common.rememberCameraManager
import common.rememberGalleryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.component.CircleButton
import presentation.component.CircleImage
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.GeneralAlertDialog
import presentation.component.ImageOptionDialog
import presentation.component.Spacer_16dp
import presentation.component.Spacer_32dp
import presentation.component.Spacer_4dp
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.component.rememberCustomImagePainter
import presentation.theme.Black
import presentation.theme.BorderColor
import presentation.theme.GrayBgOp
import presentation.theme.Transparent
import presentation.theme.orange_400
import presentation.ui.main.detail.view_model.DetailEvent
import presentation.ui.main.detail.view_model.DetailState
import presentation.ui.main.detail.view_model.DetailTexts
import presentation.ui.main.detail.view_model.DetailViewModel
import presentation.util.convertDate
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.add_to_cart
import shoping_by_kmp.shared.generated.resources.available
import shoping_by_kmp.shared.generated.resources.batch
import shoping_by_kmp.shared.generated.resources.camera_permission_message
import shoping_by_kmp.shared.generated.resources.cancel
import shoping_by_kmp.shared.generated.resources.cm
import shoping_by_kmp.shared.generated.resources.color
import shoping_by_kmp.shared.generated.resources.held_inventory
import shoping_by_kmp.shared.generated.resources.inventory
import shoping_by_kmp.shared.generated.resources.inventory_list
import shoping_by_kmp.shared.generated.resources.no
import shoping_by_kmp.shared.generated.resources.no_inventory_for_this_product
import shoping_by_kmp.shared.generated.resources.open_product_page
import shoping_by_kmp.shared.generated.resources.permission_required_dialog_title
import shoping_by_kmp.shared.generated.resources.product_details
import shoping_by_kmp.shared.generated.resources.product_diff_level
import shoping_by_kmp.shared.generated.resources.settings
import shoping_by_kmp.shared.generated.resources.sku
import shoping_by_kmp.shared.generated.resources.supplier
import shoping_by_kmp.shared.generated.resources.update_inventory
import shoping_by_kmp.shared.generated.resources.v3
import shoping_by_kmp.shared.generated.resources.v4
import shoping_by_kmp.shared.generated.resources.yes


@OptIn(ExperimentalResourceApi::class)
@Composable
fun DetailScreen(
    popup: () -> Unit,
    navigateToMoreComment: (String) -> Unit,
    state: DetailState,
    events: (DetailEvent) -> Unit,
    viewModel: DetailViewModel
) {
    val showDialog = viewModel.showDialog
    val coroutineScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var launchCamera by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(false) }
    var launchSetting by remember { mutableStateOf(false) }

    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus) {
            when (status) {
                PermissionStatus.GRANTED -> when (permissionType) {
                    PermissionType.CAMERA -> launchCamera = true
                    PermissionType.GALLERY -> launchGallery = true
                }

                else -> events(DetailEvent.OnUpdatePermissionDialog(UIComponentState.Show))
            }
        }
    })

    val cameraManager = rememberCameraManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) { it?.toImageBitmap() }
            imageBitmap = bitmap
        }
    }

    val galleryManager = rememberGalleryManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) { it?.toImageBitmap() }
            imageBitmap = bitmap
        }
    }
    LaunchedEffect(imageBitmap) {
        imageBitmap?.let {
            events(DetailEvent.OnAddImage(it)) // Trigger upload event
            imageBitmap = null
        }
    }
    if (state.imageOptionDialog == UIComponentState.Show) {
        ImageOptionDialog(onDismissRequest = {
            events(DetailEvent.OnUpdateImageOptionDialog(UIComponentState.Hide))
        }, onGalleryRequest = {
            launchGallery = true
        }, onCameraRequest = {
            launchCamera = true
        })
    }
    if (launchGallery) {
        if (permissionsManager.isPermissionGranted(PermissionType.GALLERY)) {
            galleryManager.launch()
        } else {
            permissionsManager.AskPermission(PermissionType.GALLERY)
        }
        launchGallery = false
    }
    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.AskPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }
    if (launchSetting) {
        permissionsManager.LaunchSettings()
        launchSetting = false
    }
    if (state.permissionDialog == UIComponentState.Show) {
        GeneralAlertDialog(
            title = stringResource(Res.string.permission_required_dialog_title),
            message = stringResource(Res.string.camera_permission_message),
            positiveButtonText = stringResource(Res.string.settings),
            negativeButtonText = stringResource(Res.string.cancel),
            onDismissRequest = {
                events(DetailEvent.OnUpdatePermissionDialog(UIComponentState.Hide))
            },
            onPositiveClick = { launchSetting = true },
            onNegativeClick = {}
        )
    }

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
//                                    if (imageBitmap != null) {
//                                        events(DetailEvent.OnAddImage(imageBitmap))
//                                    }
                                    item {
                                        CameraButton {
                                            events(
                                                DetailEvent.OnUpdateImageOptionDialog(
                                                    UIComponentState.Show
                                                )
                                            )
                                        }
                                    }
                                    items(state.product.gallery) {
                                        ImageSliderBox(it) {
                                            events(DetailEvent.OnUpdateSelectedImage(it))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer_8dp()

                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        val batches = viewModel.state.value.productInventoryBatch.batchesList
                        Spacer_8dp()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = state.product.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer_8dp()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.Top, // Changed to top alignment
                            horizontalArrangement = Arrangement.SpaceBetween
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
                            Text(
                                text = viewModel.getProductPrice(state.product),
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 14.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }


                        Spacer_8dp()

                        setDiffLevelText(state.product)

                        setProductPageText(state.product, viewModel)

                        // a grid that shows the size selection if exists
                        Selections(state.product, events)

                        Spacer_16dp()

                        Text(
                            stringResource(Res.string.product_details),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer_8dp()


                        val productDescription =
                            getProductDescription(state.product, viewModel.heldInventoryText.value)
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun setDiffLevelText(product: ProductSelectable) {
    val containsV3 = product.name.contains("v3", ignoreCase = true)
    val containsV4 = product.name.contains("v4", ignoreCase = true)

    var showDialog by remember { mutableStateOf(false) }
    val imageResId = if (containsV4) Res.drawable.v4 else Res.drawable.v3

    if (containsV3 || containsV4) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(stringResource(Res.string.product_diff_level))
                }
            },
            color = Color(0xFF89CFF0),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showDialog = true },
            style = MaterialTheme.typography.titleLarge
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = { showDialog = false }),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.35f),
                ) {
                    Image(
                        painter = painterResource(imageResId),
                        contentDescription = "Diff Level Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun setProductPageText(product: ProductSelectable, viewModel: DetailViewModel) {
    if (product.pdfUrl.isNotEmpty())
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(stringResource(Res.string.open_product_page))
                }
            },
            color = Color(0xFF89CFF0),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { viewModel.openPdf(product.pdfUrl) },
            style = MaterialTheme.typography.titleLarge
        )
}

@Composable
fun ColorGrid(selection: Selection, events: (DetailEvent) -> Unit, product: ProductSelectable) {
    if (selection.selector?.selected is ColorSelectable) {
        selection.selector.selectionDesc?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp, start = 12.dp),
            )
        }
        Spacer_8dp()
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(selection.selectionList.orEmpty()) { color ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            color._id?.let { it1 ->
                                selection.selector.selected = color
                                events(DetailEvent.SelectColor(it1, product))
                            }
                        }
                        .padding(5.dp)
                        .border(
                            width = 2.dp,
                            color = if ((selection.selector.selected as ColorSelectable)._id == color._id) Black else GrayBgOp,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    ColorBox((color as ColorSelectable).hex, color.img)  // Now passing the image URL as well
                }
            }
        }
    }
}


@Composable
fun ProductGrid(selection: Selection, events: (DetailEvent) -> Unit) {
    val listState = rememberLazyListState()
    val selectedProduct = selection.selector?.selected as? ProductSelectable
    val initialIndex = maxOf(0, selection.selectionList?.indexOfFirst { it._id == selectedProduct?._id } ?: 0)

    LaunchedEffect(key1 = initialIndex, key2 = selection.selectionList) {
        if (!selection.selectionList.isNullOrEmpty() && initialIndex < selection.selectionList.size) {
            listState.scrollToItem(initialIndex)
        }
    }

    if (selection.selector?.selected is ProductSelectable) {
        selection.selector.selectionDesc?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp, start = 12.dp),
            )
        }
        Spacer_8dp()
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(selection.selectionList ?: listOf()) { _, selectable ->
                ProductCard(selectable as ProductSelectable, selection, events)
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
            .width(135.dp)
            .clickable {
                product._id?.let { productId ->
                    selection.selector?.selected = product
                    events(DetailEvent.SelectProduct(productId, product))
                }
            }
            .border(
                width = 2.dp,
                color = if ((selection.selector?.selected as ProductSelectable)._id == product._id) Black else Transparent,
                shape = MaterialTheme.shapes.small
            )
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
                    .height(90.dp)
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
fun ColorBox(colorHex: String?, imageUrl: String? = null) {
    if (colorHex.isNullOrEmpty() && !imageUrl.isNullOrEmpty()) {
        // Display the image if the hex code is empty but an image URL is provided
        Image(
            painter = rememberCustomImagePainter(imageUrl),
            contentDescription = "Color Image", // Provide appropriate content description
            modifier = Modifier
                .size(32.dp) // Adjust size as needed
                .clip(RoundedCornerShape(8.dp))
        )
    } else if (!colorHex.isNullOrEmpty()) {
        // Parse the hex code to a color and display it
        val composeColor = Color(colorHex.toColorInt())
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(composeColor) // Set background color
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}


@OptIn(ExperimentalResourceApi::class)
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
                Text(stringResource(Res.string.sku), style = MaterialTheme.typography.titleMedium)
                Text(product.getCalculatedSku(), style = MaterialTheme.typography.titleLarge)
            }

            DefaultButton(
                modifier = Modifier.fillMaxWidth(.7f).height(DEFAULT__BUTTON_SIZE),
                text = stringResource(Res.string.add_to_cart)
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
    getCustomizationSteps(product = product, originalProduct = product).forEach {
        SizeGrid(it, events, product)
        ColorGrid(it, events, product)
        ProductGrid(it, events)
    }
}

@Composable
fun SizeGrid(selection: Selection, events: (DetailEvent) -> Unit, product: ProductSelectable) {
    if (selection.selector?.selected is SizeSelectable) {
        selection.selector.selectionDesc?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp, start = 12.dp),
            )
        }
        Spacer_8dp()
        // Calculate the number of rows needed
        val numRows = (selection.selectionList?.size ?: (0 + 2)) / 3

        Column(modifier = Modifier.padding(8.dp)) {
            for (row in 0 until numRows) {
                Row {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        if (index < (selection.selectionList?.size ?: 0)) {
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun InventoryStatusText(viewModel: DetailViewModel, onDialogRequest: () -> Unit) {
    var inventoryStatus = viewModel.inventoryStatusText.value
    val inventoryColor = viewModel.inventoryStatusColor.value
    val isClickable = viewModel.inventoryClickable.value
    val isUnderline = viewModel.inventoryUnderLine.value
    val isLoading = viewModel.isLoading.value

    when (inventoryStatus) {
        DetailTexts().inventoryUpdate -> {
            inventoryStatus = stringResource(Res.string.update_inventory)
        }

        DetailTexts().inventoryNotAvailable -> {
            inventoryStatus = stringResource(Res.string.no_inventory_for_this_product)
        }

        DetailTexts().inventoryList -> {
            inventoryStatus = stringResource(Res.string.inventory_list)
        }

        DetailTexts().singleInventoryResult -> {

            viewModel.formattedFreeQuantity
            inventoryStatus =
                stringResource(Res.string.inventory) + ":" + viewModel.formattedQuantity + "," + stringResource(
                    Res.string.available
                ) + ":" + viewModel.formattedFreeQuantity
        }
    }

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

    val displayText =
        if (isLoading) stringResource(Res.string.update_inventory) + " $dots " else inventoryStatus

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
            .padding(8.dp)
    )
}

@OptIn(ExperimentalResourceApi::class)
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
            text = stringResource(Res.string.batch),
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.inventory),
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.available),
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
@OptIn(ExperimentalResourceApi::class)
fun getProductDescription(product: ProductSelectable, heldInventory: String): AnnotatedString {
    val isHeld = when (heldInventory) {
        DetailTexts().no -> {
            stringResource(Res.string.no)
        }

        DetailTexts().yes -> {
            stringResource(Res.string.yes)
        }

        else -> {
            stringResource(Res.string.no)
        }
    }

    return buildAnnotatedString {
        if (product.supplier.companyName?.isNotEmpty() == true) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(Res.string.supplier) + ": ")
            }
            append("${product.supplier.companyName}\n")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(Res.string.held_inventory) + ": ")
        }
        append("$isHeld\n")
        val customizationSteps = getCustomizationSteps(
            product = product, originalProduct = product
        )
        println("TAMIRRRR --> ${customizationSteps.size}")
        customizationSteps.forEach { selection ->
            println("TAMIRRRR --> ${selection.selector?.selectionDesc}")
        }
        customizationSteps.map { selection ->
            when (val selected = selection.selector?.selected) {
                is ColorSelectable -> {
                    selected.name?.let { color ->
                        if (color.isNotEmpty()) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(Res.string.color) + ": ")
                            }
                            append("$color\n")
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
                    } else {
                        println("selected name is Empty")
                    }
                }

                is SizeSelectable -> {
                    selected.size?.let { size ->
                        if (size.isNotEmpty() && size != "0") {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${selection.selector.selectionDesc}: ")
                            }
                            append("$size ${stringResource(Res.string.cm)}\n")
                        }
                    }
                }

                null -> {}
            }
        }
    }
}

@Composable
fun CameraButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(65.dp)
            .clip(MaterialTheme.shapes.small)
            .background(Color.LightGray)
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center,

        ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Add Image",
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )
    }
}






