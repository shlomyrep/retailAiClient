package presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import presentation.theme.BorderColor
import presentation.theme.DefaultTextFieldTheme
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.address
import retailai.shared.generated.resources.city
import retailai.shared.generated.resources.country
import retailai.shared.generated.resources.submit
import retailai.shared.generated.resources.zip_code


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun AddAddressDialog(
    onDismissRequest: () -> Unit,
    onExecute: (String, String, String, String, String) -> Unit
) {

    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }


    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth(0.9f).background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
        ) {

            Spacer_16dp()

            Text(
                "Add",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer_32dp()

            Text(
                stringResource(Res.string.country) + ":",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )


            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, MaterialTheme.shapes.small),
                value = country, onValueChange = { country = it },
                colors = DefaultTextFieldTheme(),
                shape = MaterialTheme.shapes.small,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                ),
            )
            Spacer_8dp()
            Text(
                stringResource(Res.string.city) + ":",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )


            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, MaterialTheme.shapes.small),
                value = city, onValueChange = { city = it },
                colors = DefaultTextFieldTheme(),
                shape = MaterialTheme.shapes.small,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                ),
            )
            Spacer_8dp()
            Text(
                stringResource(Res.string.address) + ":",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )


            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, MaterialTheme.shapes.small),
                value = address, onValueChange = { address = it },
                colors = DefaultTextFieldTheme(),
                shape = MaterialTheme.shapes.small,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                ),
            )
            Spacer_8dp()
            Text(
                stringResource(Res.string.zip_code) + ":",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )


            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, MaterialTheme.shapes.small),
                value = zipCode, onValueChange = { zipCode = it },
                colors = DefaultTextFieldTheme(),
                shape = MaterialTheme.shapes.small,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                ),
            )




            Spacer_16dp()

            DefaultButton(
                modifier = Modifier.fillMaxWidth().height(DEFAULT__BUTTON_SIZE),
                text = stringResource(Res.string.submit)
            ) {
                onExecute(address, country, city, state, zipCode)
                onDismissRequest()
            }

            Spacer_16dp()
        }

    }


}