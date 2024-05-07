package presentation.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import business.domain.main.SalesMan
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.component.DEFAULT__BUTTON_SIZE_EXTRA
import presentation.component.DefaultButton
import presentation.component.DefaultScreenUI
import presentation.component.PasswordTextField
import presentation.component.Spacer_32dp
import presentation.component.Spacer_4dp
import presentation.component.Spacer_8dp
import presentation.theme.Blue
import presentation.theme.DefaultTextFieldTheme
import presentation.ui.splash.view_model.LoginEvent
import presentation.ui.splash.view_model.LoginState
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.choose_salesman
import shoping_by_kmp.shared.generated.resources.email
import shoping_by_kmp.shared.generated.resources.enter_valid_email
import shoping_by_kmp.shared.generated.resources.enter_valid_password
import shoping_by_kmp.shared.generated.resources.password
import shoping_by_kmp.shared.generated.resources.retail_ai_logo
import shoping_by_kmp.shared.generated.resources.select
import shoping_by_kmp.shared.generated.resources.sign_in

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoginScreen(
    state: LoginState,
    events: (LoginEvent) -> Unit,
    navigateToMain: () -> Unit,
    navigateToRegister: () -> Unit
) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DefaultScreenUI(
            queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(LoginEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.retail_ai_logo), // Make sure you have this drawable in your resources.
                    contentDescription = "Retail AI Logo",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(width = 350.dp, height = 200.dp)
                )
                Spacer_32dp()
                if (state.salesMans?.users?.isNotEmpty() == true) {
                    showUserSelection(state, events, navigateToMain)
                } else {
                    showLoginForm(state, events)
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun showUserSelection(state: LoginState, events: (LoginEvent) -> Unit, navigateToMain: () -> Unit) {
    var selectedSalesman by remember { mutableStateOf<SalesMan?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.choose_salesman),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer_32dp()

        val sortedSalesmen = state.salesMans?.users?.sortedBy { it.username } ?: listOf()

        LazyColumn(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .fillMaxWidth()
        ) {
            items(sortedSalesmen.size) { index ->
                val salesman = sortedSalesmen[index]
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(
                            width = 1.dp,
                            color = if (salesman == selectedSalesman) Blue else Color.Gray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            selectedSalesman = salesman
                            events(LoginEvent.SelectSalesMan(salesman))
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = salesman.username,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (salesman == selectedSalesman) Blue else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        DefaultButton(
            progressBarState = state.progressBarState,
            text = stringResource(Res.string.select),
            modifier = Modifier
                .fillMaxWidth()
                .height(DEFAULT__BUTTON_SIZE_EXTRA),
            onClick = {
                if (selectedSalesman != null) {
                    events(LoginEvent.SelectSalesMan(salesMan = selectedSalesman!!))
                    navigateToMain() // Trigger the navigation after the selection is confirmed.
                }
            },
            enabled = selectedSalesman != null
        )
        Spacer(Modifier.height(32.dp))
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun showLoginForm(
    state: LoginState,
    events: (LoginEvent) -> Unit
) {
    var isUsernameError by rememberSaveable { mutableStateOf(false) }
    var isPasswordError by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(stringResource(Res.string.email))
        Spacer_4dp()
        TextField(
            isError = isUsernameError,
            value = state.usernameLogin,
            onValueChange = {
                if (it.length < 32) {
                    events(LoginEvent.OnUpdateUsernameLogin(it))
                    isUsernameError = it.isEmpty()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = DefaultTextFieldTheme(),
            shape = MaterialTheme.shapes.small,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email,
            ),
        )
        AnimatedVisibility(visible = isUsernameError) {
            Text(
                stringResource(Res.string.enter_valid_email),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer_8dp()

        Text(stringResource(Res.string.password))
        Spacer_4dp()
        PasswordTextField(
            // isError = isPasswordError,
            value = state.passwordLogin,
            onValueChange = {
                events(LoginEvent.OnUpdatePasswordLogin(it))
                isPasswordError = it.length < 8
            },
            modifier = Modifier.fillMaxWidth(),
        )

        AnimatedVisibility(visible = isPasswordError) {
            Text(
                stringResource(Res.string.enter_valid_password),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer_32dp()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                progressBarState = state.progressBarState,
                text = stringResource(Res.string.sign_in),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DEFAULT__BUTTON_SIZE_EXTRA),
                onClick = { events(LoginEvent.Login) }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}
