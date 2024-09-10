package presentation.ui.main.checkout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import presentation.component.DEFAULT__BUTTON_SIZE
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_16dp
import presentation.component.Spacer_32dp
import presentation.theme.DefaultTextFieldTheme
import presentation.ui.main.checkout.view_model.CheckoutEvent
import presentation.ui.main.checkout.view_model.CheckoutState
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.first_name
import retailai.shared.generated.resources.last_name
import retailai.shared.generated.resources.save_spec
import retailai.shared.generated.resources.spec_info


@OptIn(ExperimentalResourceApi::class)
@Composable
fun CheckoutScreen(
    state: CheckoutState, events: (CheckoutEvent) -> Unit, navigateToAddress: () -> Unit, popup: () -> Unit
) {
    var customerIdError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = state.buyingSuccess) {
        if (state.buyingSuccess) {
            popup()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DefaultScreenUI(
            queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(CheckoutEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState,
            networkState = state.networkState,
            onTryAgain = { events(CheckoutEvent.OnRetryNetwork) },
            titleToolbar = stringResource(Res.string.spec_info),
            startIconToolbar = Icons.AutoMirrored.Filled.ArrowBack,
            onClickStartIconToolbar = popup
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp).align(Alignment.TopCenter)
                ) {
                    Spacer_32dp()
                    TextField(
                        value = state.firstName,
                        onValueChange = {
                            events(CheckoutEvent.OnUpdateFirstName(it))
                        },
                        enabled = true,
                        label = {
                            Text(stringResource(Res.string.first_name))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = DefaultTextFieldTheme(),
                        shape = MaterialTheme.shapes.small,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                        ),
                    )
                    Spacer_16dp()
                    TextField(
                        value = state.lastName,
                        onValueChange = {
                            events(CheckoutEvent.OnUpdateLastName(it))
                        },
                        enabled = true,
                        label = {
                            Text(stringResource(Res.string.last_name))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = DefaultTextFieldTheme(),
                        shape = MaterialTheme.shapes.small,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text,
                        ),
                    )

                    if (customerIdError != null) {
                        Text(
                            text = customerIdError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer_32dp()
                }

                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                    CheckoutButtonBox(
                        state.firstName, state.lastName, state.customerID, state.customerIdRegex
                    ) {
                        events(CheckoutEvent.BuyProduct)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun CheckoutButtonBox(
    firstName: String, lastName: String, customerID: String, customerIdRegex: String, onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), shape = RoundedCornerShape(
            topStart = 8.dp, topEnd = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {

            Spacer_16dp()
            DefaultButton(
                modifier = Modifier.fillMaxWidth().height(DEFAULT__BUTTON_SIZE),
                text = stringResource(Res.string.save_spec),
                enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && (customerID.isEmpty() || isValidCustomerId(
                    customerIdRegex, customerID
                ))
            ) {
                onClick()
            }
        }
    }
}


fun isValidCustomerId(customerIdRegex: String, customerId: String): Boolean {
    val regex = Regex(customerIdRegex)
    return regex.matches(customerId)
}

