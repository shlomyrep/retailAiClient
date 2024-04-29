package presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.ok


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ShowSnackbar(
    modifier: Modifier,
    snackbarVisibleState:  Boolean,
    title: String,
    onDismiss: () -> Unit
) {


    if (snackbarVisibleState ) {
        // Show the Snackbar
        Snackbar(
            modifier = modifier.padding(16.dp),
            action = {
                Button(onClick = {
                    // Dismiss the Snackbar
                    onDismiss()
                }) {
                    Text(text = stringResource(Res.string.ok))
                }
            }
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}