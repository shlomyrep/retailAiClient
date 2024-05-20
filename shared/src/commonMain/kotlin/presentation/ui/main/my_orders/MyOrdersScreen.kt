package presentation.ui.main.my_orders

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import business.constants.ORDER_ACTIVE
import business.constants.ORDER_CANCELED
import business.constants.ORDER_SUCCESS
import business.domain.main.Order
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.theme.BorderColor
import presentation.ui.main.my_orders.view_model.MyOrdersEvent
import presentation.ui.main.my_orders.view_model.MyOrdersState
import presentation.util.convertDate
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.add_to_cart
import shoping_by_kmp.shared.generated.resources.address
import shoping_by_kmp.shared.generated.resources.amount
import shoping_by_kmp.shared.generated.resources.arrow_down
import shoping_by_kmp.shared.generated.resources.default_image_loader
import shoping_by_kmp.shared.generated.resources.delivery_cost
import shoping_by_kmp.shared.generated.resources.delivery_type
import shoping_by_kmp.shared.generated.resources.no_orders
import shoping_by_kmp.shared.generated.resources.orders
import shoping_by_kmp.shared.generated.resources.promo_code
import kotlin.random.Random


@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun MyOrdersScreen(state: MyOrdersState, events: (MyOrdersEvent) -> Unit, popup: () -> Unit) {

    val scope = rememberCoroutineScope()

    val tabList by remember {
        mutableStateOf(
            listOf(
                "פעיל",
                "הושלם",
                "בוטל",
            )
        )
    }


    val pagerState = rememberPagerState { tabList.size }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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

            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(modifier = Modifier.height(50.dp).fillMaxWidth(),
                    selectedTabIndex = pagerState.currentPage,
                    contentColor = Color.Transparent,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                .height(4.dp).padding(horizontal = 28.dp).background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }) {
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
                            }, selected = pagerState.currentPage == index, onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            })
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
                                MyOrdersList(events, list = state.orders.filter { it.status == ORDER_ACTIVE })
                            }

                            ORDER_SUCCESS -> {
                                MyOrdersList(events, list = state.orders.filter { it.status == ORDER_SUCCESS })
                            }

                            ORDER_CANCELED -> {
                                MyOrdersList(events, list = state.orders.filter { it.status == ORDER_CANCELED })
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
private fun MyOrdersList(events: (MyOrdersEvent) -> Unit, list: List<Order>) {
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
            OrderBox(events, it)
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun OrderBox(events: (MyOrdersEvent) -> Unit, order: Order) {
    var isExpanded by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(350, easing = LinearEasing)
    )


    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                .border(1.dp, BorderColor, MaterialTheme.shapes.medium)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(order.createdAt.convertDate(), style = MaterialTheme.typography.bodyLarge)
                Icon(
                    painter = painterResource(Res.drawable.arrow_down),
                    null,
                    modifier = Modifier.size(35.dp).padding(4.dp).rotate(rotationState)
                        .noRippleClickable { isExpanded = !isExpanded })
            }
            Spacer_8dp()
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(Res.string.promo_code), style = MaterialTheme.typography.bodyLarge)
                Text(order.code, style = MaterialTheme.typography.bodyMedium)
            }
            DefaultButton(
                modifier = Modifier.fillMaxWidth(.7f).height(DEFAULT__BUTTON_SIZE),
                text = stringResource(Res.string.add_to_cart)
            ) {
                events(MyOrdersEvent.OnSendQuote(2, order))
            }

            Spacer_8dp()

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(order.products) {
                    AsyncImage(
                        it.image,
                        null,
                        modifier = Modifier.size(55.dp).padding(horizontal = 4.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(Res.drawable.default_image_loader),
                        placeholder = painterResource(Res.drawable.default_image_loader)
                    )
                }
            }
            Spacer_8dp()

            if (isExpanded) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(Res.string.amount), style = MaterialTheme.typography.bodyLarge)
//                            Text(order.getAmount(), style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer_8dp()

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(Res.string.delivery_cost), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                order.shippingType.getPrice(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer_8dp()

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(Res.string.delivery_type), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                order.shippingType.title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer_8dp()


                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(stringResource(Res.string.address), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                order.address.getShippingAddress(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            Spacer_8dp()

        }
    }
}
