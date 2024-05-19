package presentation.ui.main.cart

import ExpandingText
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import business.datasource.network.main.responses.ColorSelectable
import business.datasource.network.main.responses.ProductSelectable
import business.datasource.network.main.responses.SizeSelectable
import business.datasource.network.main.responses.getCustomizationSteps
import business.domain.main.Basket
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.component.rememberCustomImagePainter
import presentation.theme.BorderColor
import presentation.theme.ProgressBarColor
import presentation.theme.Red
import presentation.theme.TextFieldColor
import presentation.theme.Transparent
import presentation.ui.main.cart.view_model.CartEvent
import presentation.ui.main.cart.view_model.CartState
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.basket_is_empty
import shoping_by_kmp.shared.generated.resources.cm
import shoping_by_kmp.shared.generated.resources.color
import shoping_by_kmp.shared.generated.resources.continued
import shoping_by_kmp.shared.generated.resources.delete
import shoping_by_kmp.shared.generated.resources.general_bath
import shoping_by_kmp.shared.generated.resources.general_bath_first_floor
import shoping_by_kmp.shared.generated.resources.general_bath_sanitary_fixtures
import shoping_by_kmp.shared.generated.resources.general_bathroom
import shoping_by_kmp.shared.generated.resources.general_flooring
import shoping_by_kmp.shared.generated.resources.general_room_text
import shoping_by_kmp.shared.generated.resources.guest_shower_ground_floor
import shoping_by_kmp.shared.generated.resources.guest_toilet
import shoping_by_kmp.shared.generated.resources.guest_toilet_sanitary_fixtures
import shoping_by_kmp.shared.generated.resources.it_corner
import shoping_by_kmp.shared.generated.resources.kitchen
import shoping_by_kmp.shared.generated.resources.living_kitchen_and_passages
import shoping_by_kmp.shared.generated.resources.parents_bathroom
import shoping_by_kmp.shared.generated.resources.parents_shower
import shoping_by_kmp.shared.generated.resources.parents_shower_first_floor
import shoping_by_kmp.shared.generated.resources.parents_shower_sanitary_fixtures
import shoping_by_kmp.shared.generated.resources.room_name_header_text
import shoping_by_kmp.shared.generated.resources.spec
import shoping_by_kmp.shared.generated.resources.supplier


@OptIn(ExperimentalResourceApi::class)
@Composable
fun CartScreen(
    state: CartState,
    events: (CartEvent) -> Unit,
    navigateToDetail: (String) -> Unit,
    navigateToCheckout: () -> Unit,
) {

    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(CartEvent.OnRemoveHeadFromQueue) },
        progressBarState = state.progressBarState,
        networkState = state.networkState,
        titleToolbar = stringResource(Res.string.spec),
        onTryAgain = { events(CartEvent.OnRetryNetwork) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().align(Alignment.Center).padding(bottom = 100.dp)
            ) {
                items(state.baskets) {
                    CartBox(
                        events,
                        it,
                        navigateToDetail = navigateToDetail
                    ) {
                        events(CartEvent.DeleteFromBasket(it.product.id))
                    }
                }
            }

            if (state.baskets.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                    ProceedButtonBox() {
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
                        fontSize = 30.sp,
                        text = stringResource(Res.string.basket_is_empty),
                        style = MaterialTheme.typography.labelLarge,
                        color = BorderColor,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProceedButtonBox(onClick: () -> Unit) {

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

            Spacer_8dp()
            DefaultButton(
                modifier = Modifier.fillMaxWidth().height(DEFAULT__BUTTON_SIZE),
                text = stringResource(Res.string.continued)
            ) {
                onClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBox(
    events: (CartEvent) -> Unit,
    basket: Basket,
    navigateToDetail: (String) -> Unit,
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
                    events,
                    basket,
                    navigateToDetail = navigateToDetail
                )
            })
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DismissCartContent(
    events: (CartEvent) -> Unit,
    basket: Basket,
    navigateToDetail: (String) -> Unit,
) {
    val roomNames = listOf(
        stringResource(Res.string.general_room_text),
        stringResource(Res.string.general_bath),
        stringResource(Res.string.parents_shower),
        stringResource(Res.string.kitchen),
        stringResource(Res.string.general_bathroom),
        stringResource(Res.string.guest_toilet),
        stringResource(Res.string.parents_bathroom),
        stringResource(Res.string.general_bath_sanitary_fixtures),
        stringResource(Res.string.parents_shower_sanitary_fixtures),
        stringResource(Res.string.general_flooring),
        stringResource(Res.string.parents_shower_first_floor),
        stringResource(Res.string.it_corner),
        stringResource(Res.string.guest_shower_ground_floor),
        stringResource(Res.string.general_bath_first_floor),
        stringResource(Res.string.living_kitchen_and_passages),
        stringResource(Res.string.guest_toilet_sanitary_fixtures)
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedRoomName by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { focusManager.clearFocus() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .noRippleClickable {
                        navigateToDetail(basket.product.id)
                    }
            ) {
                Image(
                    painter = rememberCustomImagePainter(basket.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Box {
                        OutlinedTextField(
                            value = selectedRoomName,
                            onValueChange = {
                                selectedRoomName = it
                            },
                            enabled = true,
                            label = {
                                Text(stringResource(Res.string.room_name_header_text))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    if (isFocused && !focusState.isFocused) {
                                        if (selectedRoomName.isNotEmpty()){
                                            basket.product.roomName = selectedRoomName
                                            events(CartEvent.AddProduct(basket.product))
                                            isFocused = false
                                        }
                                    } else if (focusState.isFocused) {
                                        isFocused = true
                                    }
                                },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Transparent,
                                unfocusedContainerColor = Transparent,
                                cursorColor = MaterialTheme.colorScheme.onBackground,
                                focusedIndicatorColor = ProgressBarColor,
                                unfocusedIndicatorColor = ProgressBarColor,
                                disabledContainerColor = TextFieldColor,
                                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                disabledIndicatorColor = ProgressBarColor,
                            ),
                            shape = MaterialTheme.shapes.small,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text,
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.clickable { expanded = !expanded }
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            roomNames.forEach { roomName ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedRoomName = roomName
                                        basket.product.roomName = roomName
                                        events(CartEvent.AddProduct(basket.product))
                                        expanded = false
                                        focusManager.clearFocus()
                                    },
                                    text = { Text(roomName) }
                                )
                            }
                        }
                    }
                }
                Text(
                    text = basket.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = basket.category.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 18.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                val productDescription = getProductDescription(basket.product)
                ExpandingText(
                    modifier = Modifier.fillMaxWidth(),
                    text = productDescription,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)
                ) {}
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}




@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        Red.copy(alpha = .2f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = .2f)
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                Icons.Default.Delete,
                tint = Red,
                contentDescription = stringResource(Res.string.delete),
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier)
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun getProductDescription(product: ProductSelectable): AnnotatedString {

    return buildAnnotatedString {
        if (product.supplier.companyName?.isNotEmpty() == true) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(Res.string.supplier) + ": ")
            }
            append("${product.supplier.companyName}\n")
        }

        val customizationSteps = getCustomizationSteps(
            product = product, originalProduct = product
        )
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



