package presentation.ui.main.my_orders

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import business.constants.ORDER_ACTIVE
import business.constants.ORDER_CANCELED
import business.constants.ORDER_SUCCESS
import business.domain.main.Order
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_12dp
import presentation.component.Spacer_8dp
import presentation.theme.BorderColor
import presentation.theme.ProgressBarColor
import presentation.theme.TextFieldColor
import presentation.theme.Transparent
import presentation.ui.main.my_orders.view_model.MyOrdersEvent
import presentation.ui.main.my_orders.view_model.MyOrdersState
import presentation.ui.main.my_orders.view_model.MyOrdersViewModel
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.created_bid
import retailai.shared.generated.resources.created_pdf
import retailai.shared.generated.resources.customer_id
import retailai.shared.generated.resources.customer_name
import retailai.shared.generated.resources.date
import retailai.shared.generated.resources.default_image_loader
import retailai.shared.generated.resources.invalid_customer_id
import retailai.shared.generated.resources.no_orders
import retailai.shared.generated.resources.orders
import retailai.shared.generated.resources.show_pdf
import kotlin.random.Random


@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun MyOrdersScreen(
    state: MyOrdersState,
    events: (MyOrdersEvent) -> Unit,
    viewModel: MyOrdersViewModel,
    navigateToEditOrder: () -> Unit,
    popup: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val tabList by remember { mutableStateOf(listOf("")) }
    val pagerState = rememberPagerState { tabList.size }
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            DefaultScreenUI(
                queue = state.errorQueue,
                onRemoveHeadFromQueue = { events(MyOrdersEvent.OnRemoveHeadFromQueue) },
                progressBarState = state.progressBarState,
                networkState = state.networkState,
                onTryAgain = { events(MyOrdersEvent.OnRetryNetwork) },
                titleToolbar = stringResource(Res.string.orders),
                startIconToolbar = Icons.AutoMirrored.Filled.ArrowBack,
                onClickStartIconToolbar = popup
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabRow(
                        modifier = Modifier.height(10.dp).fillMaxWidth(),
                        selectedTabIndex = pagerState.currentPage,
                        contentColor = Color.Transparent,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                    .height(4.dp).padding(horizontal = 28.dp).background(
                                        color = Color.Transparent,
                                        shape = MaterialTheme.shapes.medium
                                    )
                            )
                        }
                    ) {
                        tabList.forEachIndexed { index, _ ->
                            Tab(
                                unselectedContentColor = Color.Transparent,
                                selectedContentColor = Color.Transparent,
                                text = {
                                    Text(
                                        tabList[index],
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.Top
                    ) { index ->
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (index) {
                                ORDER_ACTIVE -> {

                                    MyOrdersList(
                                        events,
                                        list = state.orders.filter { it.status == ORDER_ACTIVE }.sortedByDescending { it.createdAt },
                                        state,
                                        viewModel,
                                        snackbarHostState,
                                        navigateToEditOrder
                                    )
                                }

                                ORDER_SUCCESS -> {
                                    MyOrdersList(
                                        events,
                                        list = state.orders.filter { it.status == ORDER_SUCCESS },
                                        state,
                                        viewModel,
                                        snackbarHostState,
                                        navigateToEditOrder
                                    )
                                }

                                ORDER_CANCELED -> {
                                    MyOrdersList(
                                        events,
                                        list = state.orders.filter { it.status == ORDER_CANCELED },
                                        state,
                                        viewModel,
                                        snackbarHostState,
                                        navigateToEditOrder
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MyOrdersList(
    events: (MyOrdersEvent) -> Unit,
    list: List<Order>,
    state: MyOrdersState,
    viewModel: MyOrdersViewModel,
    snackbarHostState: SnackbarHostState,
    navigateToEditOrder: () -> Unit,
) {
    if (list.isEmpty()) {
        Text(
            stringResource(Res.string.no_orders),
            style = MaterialTheme.typography.titleLarge,
            color = BorderColor,
            modifier = Modifier.fillMaxSize().padding(top = 64.dp),
            textAlign = TextAlign.Center
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(list, key = { Random.nextInt().toString() }) {
            OrderBox(events, it, state, viewModel, snackbarHostState, navigateToEditOrder)
        }
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun OrderBox(
    events: (MyOrdersEvent) -> Unit,
    order: Order,
    state: MyOrdersState,
    viewModel: MyOrdersViewModel,
    snackbarHostState: SnackbarHostState,
    navigateToEditOrder: () -> Unit
) {
    var customerId by remember { mutableStateOf(order.customerId) }
    var customerIdError by remember { mutableStateOf<String?>(null) }
    val orderIdSaved by viewModel.orderIdSaved.collectAsState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, BorderColor, MaterialTheme.shapes.medium)
                .animateContentSize()
        ) {
            Spacer_8dp()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${stringResource(Res.string.date)}:  ${formatIsoStringToHebrew(order.createdAt)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = {
                        events(MyOrdersEvent.OnEditOrder(order.orderId))
                    }
                ) {
                    //TODO remove remark when edit order is working
//                    Box(
//                        modifier = Modifier
//                            .size(40.dp)
//                            .background(
//                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
//                                shape = CircleShape
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
////                        Icon(
////                            imageVector = Icons.Default.Edit,
////                            contentDescription = stringResource(Res.string.edit_order),
////                            tint = MaterialTheme.colorScheme.primary
////                        )
//                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${stringResource(Res.string.customer_name)}:  ${order.firstName} ${order.lastName}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .animateContentSize()
                    .padding(horizontal = 10.dp),
            ) {
                val errorMessages = stringResource(Res.string.invalid_customer_id)
                val isTextFieldEditable = order.customerId.isEmpty()
                OutlinedTextField(
                    value = customerId,
                    onValueChange = { customerIdInput ->
                        customerId = customerIdInput
                        customerIdError =
                            if (customerIdInput.isEmpty() || isValidCustomerId(state.customerIdRegex, customerIdInput)) {
                                null
                            } else {
                                errorMessages
                            }
                    },
                    enabled = isTextFieldEditable,
                    label = { Text(text = stringResource(Res.string.customer_id)) },
                    modifier = Modifier.fillMaxWidth(),
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
                        keyboardType = KeyboardType.Number,
                    )
                )
                if (customerIdError != null) {
                    Text(
                        text = customerIdError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer_12dp()

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(order.products) {
                    AsyncImage(
                        it.image,
                        null,
                        modifier = Modifier
                            .size(55.dp)
                            .padding(horizontal = 4.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(Res.drawable.default_image_loader),
                        placeholder = painterResource(Res.drawable.default_image_loader)
                    )
                }
            }
            Spacer_8dp()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DefaultButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(DEFAULT__BUTTON_SIZE)
                        .padding(end = 5.dp),
                    text = stringResource(Res.string.created_pdf)
                ) {
                    events(MyOrdersEvent.OnSendQuote(2, order))
                }

                DefaultButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(DEFAULT__BUTTON_SIZE)
                        .padding(start = 5.dp),
                    text = stringResource(Res.string.created_bid)
                ) {
                    if (order.customerId.isNotEmpty()) {
                        events(MyOrdersEvent.OnSendQuote(1, order))
                    }
                }
            }
            Spacer_8dp()

            val pdf = order.pdf.ifEmpty { state.orderPdf }
            if (pdf.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    ClickableTextWithCopy(
                        displayText = "${stringResource(Res.string.show_pdf)}  ${order.firstName} ${order.lastName}",
                        url = pdf,
                        onClick = {
                            viewModel.openPdf(pdf)
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
    LaunchedEffect(orderIdSaved) {
        if (orderIdSaved) {
            navigateToEditOrder()
            viewModel.resetOrderIdSaved()
        }
    }
}



@Composable
fun ClickableTextWithCopy(
    displayText: String,
    url: String,
    onClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append(displayText)
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = {
                        coroutineScope.launch {
                            clipboardManager.setText(AnnotatedString(url))
                            snackbarHostState.showSnackbar("Text copied to clipboard")
                        }
                    }
                )
            }
    ) {
        BasicText(
            text = annotatedString,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
        )
    }
}


fun formatIsoStringToHebrew(isoString: String): String {
    val instant = Instant.parse(isoString)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val hebrewMonths =
        listOf("ינואר", "פברואר", "מרץ", "אפריל", "מאי", "יוני", "יולי", "אוגוסט", "ספטמבר", "אוקטובר", "נובמבר", "דצמבר")
    val month = hebrewMonths[localDateTime.monthNumber - 1]

    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    val second = localDateTime.second.toString().padStart(2, '0')

    return "$month ${localDateTime.dayOfMonth}, $hour:$minute:$second"
}



fun isValidCustomerId(customerIdRegex: String, customerId: String): Boolean {
    val regex = Regex(customerIdRegex)
    return regex.matches(customerId)
}
