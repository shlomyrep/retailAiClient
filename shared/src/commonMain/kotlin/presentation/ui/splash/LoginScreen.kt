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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextDecoration
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
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.choose_salesman
import retailai.shared.generated.resources.email
import retailai.shared.generated.resources.enter_valid_email
import retailai.shared.generated.resources.enter_valid_password
import retailai.shared.generated.resources.password
import retailai.shared.generated.resources.retail_ai_logo
import retailai.shared.generated.resources.select
import retailai.shared.generated.resources.sign_in

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

            var showSalesManDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // Arrange items from the top
            ) {
                // Optionally add a small spacer to give some top padding if needed
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(Res.drawable.retail_ai_logo),
                    contentDescription = "Retail AI Logo",
                    modifier = Modifier.size(width = 350.dp, height = 200.dp)
                )
                Spacer_8dp()
                if (state.isLoginSucceeded){
                    if (state.salesMans?.users?.isNotEmpty() == true) {
                        showUserSelectionWithDialog(state, events, navigateToMain)
                    } else {
                        Spacer_32dp()
                        Spacer_32dp()
                        Text(
                            text = "הזן שם מוכרן",
                            modifier = Modifier.clickable { showSalesManDialog = true },
                            color = Blue,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }else{
                    showLoginForm(state, events)
                }
            }
            if (showSalesManDialog) {
                InsertNameDialog(
                    onDismissRequest = { showSalesManDialog = false },
                    onSave = { firstName, lastName ->
                        // Save the manually entered name
                        events(LoginEvent.OnSaveSalesManNameManually(firstName, lastName))
                        showSalesManDialog = false
                        navigateToMain()
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun showUserSelectionWithDialog(
    state: LoginState,
    events: (LoginEvent) -> Unit,
    navigateToMain: () -> Unit
) {
    var selectedSalesman by remember { mutableStateOf<SalesMan?>(null) }
    var showInsertDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.choose_salesman),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

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


            Spacer_32dp()
            DefaultButton(
                progressBarState = state.progressBarState,
                text = stringResource(Res.string.select),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DEFAULT__BUTTON_SIZE_EXTRA),
                onClick = {
                    if (selectedSalesman != null) {
                        events(LoginEvent.SelectSalesMan(selectedSalesman!!))
                        navigateToMain()
                    }
                },
                enabled = selectedSalesman != null
            )
            Spacer_32dp()
            Text(
                text = "הזן שם מוכרן",
                modifier = Modifier.clickable { showInsertDialog = true },
                color = Blue,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showInsertDialog) {
        InsertNameDialog(
            onDismissRequest = { showInsertDialog = false },
            onSave = { firstName, lastName ->
                // Save the manually entered name
                events(LoginEvent.OnSaveSalesManNameManually(firstName, lastName))
                showInsertDialog = false
                navigateToMain()
            }
        )
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
                isPasswordError = it.length < 6
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

@Composable
fun InsertNameDialog(
    onDismissRequest: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "הזן שם מוכרן") },
        text = {
            Column {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("שם פרטי") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("שם משפחה") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            DefaultButton(
                text = "שמור",
                onClick = { onSave(firstName, lastName) },
                modifier = Modifier.padding(8.dp)
            )
        },
        dismissButton = {
            DefaultButton(
                text = "ביטול",
                onClick = onDismissRequest,
                modifier = Modifier.padding(8.dp)
            )
        }
    )
}

