package presentation.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.component.MultiStateAnimationCircleFilledCanvas
import presentation.component.Spacer_32dp
import presentation.theme.ProgressBarColor
import presentation.theme.White
import presentation.theme.grey_700
import presentation.theme.splashBackground
import presentation.ui.splash.view_model.LoginEvent
import presentation.ui.splash.view_model.LoginState
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.default_image_loader
import shoping_by_kmp.shared.generated.resources.discover
import shoping_by_kmp.shared.generated.resources.enjoy
import shoping_by_kmp.shared.generated.resources.getting_start
import shoping_by_kmp.shared.generated.resources.live_your_perfect
import shoping_by_kmp.shared.generated.resources.retail_ai_logo
import shoping_by_kmp.shared.generated.resources.shop
import shoping_by_kmp.shared.generated.resources.splash_title

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun SplashScreen(
    state: LoginState,
    events: (LoginEvent) -> Unit,
    navigateToMain: () -> Unit,
    navigateToLogin: () -> Unit,
) {



    LaunchedEffect(state.navigateToMain) {
        delay(500L)
        if (state.navigateToMain) {
            navigateToMain()
        } else {
            navigateToLogin()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White) // Make sure you have this color defined in your colors.xml or use a Color directly.
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "עולם של חדשנות",
            color =  ProgressBarColor, // Make sure you have this color defined in your colors.xml or use a Color directly.
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

        Image(
            painter = painterResource(Res.drawable.retail_ai_logo), // Make sure you have this drawable in your resources.
            contentDescription = "Retail AI Logo",
            modifier = Modifier
                .padding(top = 150.dp)
                .size(width = 350.dp, height = 200.dp)
        )
    }

}